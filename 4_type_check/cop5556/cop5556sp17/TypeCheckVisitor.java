// TODO Chains have types and expressions have types.
// TODO  CHECK IF method to class Type that takes a token and returns the corresponding TypeName

package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static cop5556sp17.Scanner.*;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.AST.Type.*;
import cop5556sp17.AST.*;
import cop5556sp17.Parser.*;

public class TypeCheckVisitor implements ASTVisitor {

    @SuppressWarnings("serial")
    public static class TypeCheckException extends Exception {
        TypeCheckException(String message) {
            super(message);
        }
    }
    private TypeCheckException missingVar(String varName) throws TypeCheckException {
        return new TypeCheckException("No variable declared with identifier: " + varName);
    }

    SymbolTable symtab = new SymbolTable();

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        for (ParamDec param: program.getParams()) {
            param.visit(this, arg);
        }
        return program.getB().visit(this, arg);
    }

    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        switch (paramDec.getType().kind) {
            case KW_URL:
                paramDec.setTypeName(Type.TypeName.URL);
                break;
            case KW_FILE:
                paramDec.setTypeName(Type.TypeName.FILE);
                break;
            case KW_INTEGER:
                paramDec.setTypeName(Type.TypeName.INTEGER);
                break;
            case KW_BOOLEAN:
                paramDec.setTypeName(Type.TypeName.BOOLEAN);
                break;
            default:
                throw new TypeCheckException("Unexpected kind for paramdec: " + paramDec.getType().kind + " at " + paramDec.getFirstToken().getLinePos());
        }
        return symtab.insert(paramDec.getIdent().getText(), paramDec);
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        symtab.enterScope();

        for (Dec dec : block.getDecs()) {
            dec.visit(this, arg);
        }

        for (Statement statement : block.getStatements()) {
            statement.visit(this, arg);
        }

        symtab.leaveScope();
        return null;
    }

    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        switch (declaration.getType().kind) {
            case KW_FRAME:
                declaration.setTypeName(Type.TypeName.FRAME);
                break;
            case KW_IMAGE:
                declaration.setTypeName(Type.TypeName.IMAGE);
                break;
            case KW_INTEGER:
                declaration.setTypeName(Type.TypeName.INTEGER);
                break;
            case KW_BOOLEAN:
                declaration.setTypeName(Type.TypeName.BOOLEAN);
                break;
            default:
                throw new TypeCheckException("Unexpected kind for dec: " + declaration.getType().kind + " " + declaration.getFirstToken().getLinePos());
        }
        return symtab.insert(declaration.getIdent().getText(), declaration);
    }

    /***************************************** Statements ***********************************************/

    @Override
    public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
        sleepStatement.getE().visit(this, arg);
        if (sleepStatement.getE().getTypeName() != Type.TypeName.INTEGER) {
            throw new TypeCheckException("Sleep statement expression should be of type INTEGER, but was "
                    + sleepStatement.getE().getTypeName());
        }
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
        String varName = assignStatement.getVar().getText();
        assignStatement.getE().visit(this, arg);
        Dec dec = symtab.lookup(varName);

        if (dec == null) {
            throw missingVar(varName);
        }

        TypeName varKind = dec.getTypeName(), assignKind = assignStatement.getE().getTypeName();
        if (assignKind != varKind) {
            throw new TypeCheckException("Tried to assign type " + assignKind + " to " + varKind + " variable " + varName);
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        whileStatement.getE().visit(this, arg);
        if (whileStatement.getE().getTypeName() != Type.TypeName.BOOLEAN) {
            throw new TypeCheckException("While Statement expression should be of type BOOLEAN, but was "
                    +whileStatement.getE().getTypeName());
        }
        return null;
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        ifStatement.getE().visit(this, arg);
        if(ifStatement.getE().getTypeName() != Type.TypeName.BOOLEAN){
            throw new TypeCheckException("If Statement expression should be of type BOOLEAN, but was "
                    + ifStatement.getE().getTypeName());
        }
        return null;
    }

    /***************************************** Chains  **************************************************/

    @Override
    public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
        Dec dec = symtab.lookup(identChain.getFirstToken().getText());

        if (dec == null) {
            throw missingVar(identChain.getFirstToken().getText());
        }
        identChain.setTypeName(dec.getTypeName());
        return null;
    }

    @Override
    public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        int tupleSize = filterOpChain.getArg().getExprList().size();
        if (tupleSize != 0) {
            throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
        }
            filterOpChain.setTypeName(IMAGE);

        return null;
    }

    @Override
    public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
        int tupleSize = frameOpChain.getArg().getExprList().size();
        Scanner.Token frameOp = frameOpChain.getFirstToken();

        if(frameOp.isKind(KW_SHOW) || frameOp.isKind(KW_HIDE)){
            if(tupleSize != 0){
                throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
            }
            frameOpChain.setTypeName(NONE);
        } else if (frameOp.isKind(KW_XLOC) || frameOp.isKind(KW_YLOC)){
            if(tupleSize != 0){
                throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
            }
            frameOpChain.setTypeName(INTEGER);
        } else if (frameOp.isKind(KW_MOVE)){
            if(tupleSize != 2){
                throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
            }
            frameOpChain.setTypeName(NONE);
        } else {
            throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
        }

        return null;
    }

    @Override
    public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        Scanner.Token imageOp = imageOpChain.getFirstToken();
        int tupleSize = imageOpChain.getArg().getExprList().size();

        if (imageOp.isKind(OP_WIDTH) || imageOp.isKind(OP_HEIGHT)) {
            if (tupleSize != 0) {
                throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
            }
                imageOpChain.setTypeName(INTEGER);
        } else if (imageOp.isKind(KW_SCALE)){
            if (tupleSize != 1) {
                throw new TypeCheckException("Tuple size should be 1 for filterOpChain, but was: " + tupleSize);
            }
                imageOpChain.setTypeName(IMAGE);
        }
        return null;
    }

    @Override
    public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

        binaryChain.getE0().visit(this, arg);
        binaryChain.getE1().visit(this, arg);

        TypeName chainType = binaryChain.getE0().getTypeName();
        Kind arrow = binaryChain.getArrow().kind;
        TypeName chainElemType = binaryChain.getE1().getTypeName();

        Kind firstToken = binaryChain.getE1().firstToken.kind;

        if (arrow == BARARROW && chainType != IMAGE) {
            throw new TypeCheckException("BARROW is only compatible with a chain of type IMAGE.");
        }

        switch (chainType) {
            case URL: case FILE:
                if (chainElemType != IMAGE) {
                    throw new TypeCheckException("chainType URL/FILE must have chainElem of Type IMAGE, but found " + chainElemType);
                }
                binaryChain.setTypeName(IMAGE);
                break;
            case FRAME:
                if (!(binaryChain.getE1() instanceof FrameOpChain)) {
                    throw new TypeCheckException("If chainType is FRAME, E1 should be FrameOpChain, not "
                            + binaryChain.getE1().getClass().getSimpleName());
                }
                if (firstToken == KW_XLOC || firstToken == KW_YLOC) {
                    binaryChain.setTypeName(INTEGER);
                } else if (firstToken == KW_SHOW || firstToken == KW_HIDE || firstToken == KW_MOVE) {
                    binaryChain.setTypeName(IMAGE);
                } else {
                    throw new TypeCheckException("chainType FRAME should not have a firstToken of type " + firstToken);
                }
                break;
            case IMAGE:
                if (binaryChain.getE1() instanceof IdentChain){
                    binaryChain.setTypeName(IMAGE);
                } else if (binaryChain.getE1() instanceof ImageOpChain){
                    if (firstToken == KW_SCALE || firstToken == OP_WIDTH || firstToken == OP_HEIGHT ){
                        binaryChain.setTypeName(IMAGE);
                    }
                } else if(chainElemType == FRAME){
                    binaryChain.setTypeName(FRAME);
                } else if (chainElemType == FILE){
                    binaryChain.setTypeName(NONE);
                } else {
                    throw new TypeCheckException("chainType FRAME should not have a firstToken of type " + firstToken);
                }
                break;
            default:
                throw new TypeCheckException("Incompatible chain type: "+ chainType);
        }
        return null;
    }

    /***************************************** Expressions **********************************************/

    @Override
    public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
        Dec dec = symtab.lookup(identExpression.getFirstToken().getText());
        if (dec == null) {
            throw missingVar(identExpression.getFirstToken().getText());
        }
        identExpression.setTypeName(dec.getTypeName());
        return dec;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception { // TODO set identval == expression type ?
        Dec dec = symtab.lookup(identX.getFirstToken().getText());
        if (dec == null) {
            throw missingVar(identX.getFirstToken().getText());
        }
        identX.setDec(dec);
        return null;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        if (intLitExpression.getFirstToken().kind != Scanner.Kind.INT_LIT) {
            throw new TypeCheckException("IntLitExpression should contain an INT_LIT, but instead was: "
                    +intLitExpression.getFirstToken().kind);
        }
        intLitExpression.setTypeName(Type.TypeName.INTEGER);
        return null;
    }

    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        booleanLitExpression.setTypeName(Type.TypeName.BOOLEAN);
        return booleanLitExpression.getValue();
    }

    @Override
    public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        constantExpression.setTypeName(INTEGER);
        return constantExpression.getValue();  // TODO MAYBE?
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        Object val0 = binaryExpression.getE0().visit(this, null);
        Object val1 = binaryExpression.getE1().visit(this, null);

        TypeName type0 = binaryExpression.getE0().getTypeName();
        TypeName type1 = binaryExpression.getE1().getTypeName();
        Kind op = binaryExpression.getOp().kind;

        switch (type0) {
            case INTEGER:  case IMAGE:
                if (!(type1 == INTEGER || type1 == IMAGE)) {
                    throw new TypeCheckException("types of IMAGE/INTEGER can only be op'd with other IMAGE/INTEGERs");
                }
                Integer num0 = (Integer) val0, num1 = (Integer) val1;
                if (op == LT || op == GT || op == LE || op == GE) {
                    binaryExpression.setTypeName(BOOLEAN);
                } else {
                    binaryExpression.setTypeName(INTEGER);
                }
                if (type0 == INTEGER && type1 == INTEGER) {
                    switch (op) {
                        case LT:
                            return num0 < num1;
                        case GT:
                            return num0 > num1;
                        case LE:
                            return num0 <= num1;
                        case GE:
                            return num0 >= num1;
                        default:
                            throw new TypeCheckException("Unexpected op for IMAGE/INTEGER: " + op);
                    }
                } else {
                    binaryExpression.setTypeName(IMAGE);
                }
                switch (op) {
                    case PLUS:
                        return num0 + num1;
                    case MINUS:
                        return num0 - num1;
                    case TIMES:
                        return num0 * num1;
                    case DIV:
                        return num0 / num1;
                    default:
                        throw new TypeCheckException("Unexpected op for IMAGE/INTEGER: " + op);
                }
            case BOOLEAN:
                if (type1 != type0) {
                    throw new TypeCheckException("type0 must be boolean if Type1 is.");
                }
                binaryExpression.setTypeName(BOOLEAN);
                Boolean bool0 = (Boolean) val0, bool1 = (Boolean) val1;
                switch (op) {
                    case EQUAL:
                        return bool0 == bool1;
                    case NOTEQUAL:
                        return bool0 != bool1;
                    case LT:
                        return !bool0 && bool1;
                    case GT:
                        return bool0 && !bool1;
                    case LE:
                        return !(bool0 && !bool1);
                    case GE:
                        return !(!bool0 && bool1);
                    default:
                        throw new TypeCheckException("Wrong operator for booleans: "+op);
                }
            default:
                throw new TypeCheckException("Unrecognized type0 " + type0);
        }
    }


    @Override
    public Object visitTuple(Tuple tuple, Object arg) throws Exception {
        for (Expression expression : tuple.getExprList()){
            if (expression.getTypeName() != INTEGER){
                throw new TypeCheckException("Expected type Integer for expression, but was: " + expression.getTypeName());
            }
        }
        return null;
    }

    /******************************** End of File  ******************************************************/
}
