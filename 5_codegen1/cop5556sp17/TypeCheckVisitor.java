// TODO Chains have types and expressions have types.
// TODO  CHECK IF method to class Type that takes a token and returns the corresponding TypeName

package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static cop5556sp17.Scanner.*;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.AST.Type.*;
import cop5556sp17.AST.*;
import cop5556sp17.Parser.*;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;

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
            if (tupleSize == 0) {
                imageOpChain.setTypeName(INTEGER);
            } else {
                throw new TypeCheckException("Tuple size should be 0 for filterOpChain, but was: " + tupleSize);
            }
        } else if (imageOp.isKind(KW_SCALE)){
            if (tupleSize == 1) {
                imageOpChain.setTypeName(IMAGE);
            } else {
                throw new TypeCheckException("Tuple size should be 1 for filterOpChain, but was: " + tupleSize);
            }
        }
        return null;
    }

    @Override
    public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

        binaryChain.getE0().visit(this, arg);
        binaryChain.getE1().visit(this, arg);

        Chain chain = binaryChain.getE0();
        ChainElem chainElem = binaryChain.getE1();

        TypeName chainType = binaryChain.getE0().getTypeName();
        Kind arrow = binaryChain.getArrow().kind;
        TypeName chainElemType = binaryChain.getE1().getTypeName();

        Kind firstToken = binaryChain.getE1().firstToken.kind;

        if (arrow == BARARROW){
           if( chainType == IMAGE){
               if (chainElem instanceof FilterOpChain && ((FilterOpChain) chainElem).firstToken.isKind(OP_GRAY, OP_BLUR, OP_CONVOLVE)) {
                   binaryChain.setTypeName(IMAGE);
                   return null;
               } else {
                   throw new TypeCheckException("BARARROW is only compatible with a ElementChain of instance of FilterOpChain or kind OP_GRAY, OP_BLUR, OP_CONVOLVE.");
               }
           } else {
               throw new TypeCheckException("BARARROW is only compatible with a chain of type IMAGE.");
           }
        }

        switch (chain.getTypeName()) {
            case URL: case FILE:
                if (chainElemType != IMAGE) {
                    throw new TypeCheckException("chain of Type URL/FILE must have chainElem of Type IMAGE, but found " + chainElemType);
                }
                binaryChain.setTypeName(IMAGE);
                break;
            case FRAME:
                if ((chainElem instanceof FrameOpChain)) {
                    if (chainElem.firstToken.isKind(KW_XLOC) || chainElem.firstToken.isKind(KW_YLOC)) {
                        binaryChain.setTypeName(INTEGER);
                    } else if (firstToken == KW_SHOW || firstToken == KW_HIDE || firstToken == KW_MOVE) {
                        binaryChain.setTypeName(FRAME);
                    } else {
                        throw new TypeCheckException("chain of Type FRAME should not have a firstToken of Type " + binaryChain.getE1().getTypeName());
                    }
                } else {
                    throw new TypeCheckException("If chainType is FRAME, E1 should be FrameOpChain, not "
                            + binaryChain.getE1().getClass().getSimpleName());
                }

                break;
            case IMAGE:
                if (chainElem.getTypeName() == FRAME){
                    binaryChain.setTypeName(FRAME);
                } else if (chainElem instanceof ImageOpChain && ((ImageOpChain) chainElem).firstToken.isKind(OP_HEIGHT, OP_WIDTH)){
                    binaryChain.setTypeName(INTEGER);
                } else if (chainElem instanceof ImageOpChain && ((ImageOpChain) chainElem).firstToken.isKind(KW_SCALE)) {
                    binaryChain.setTypeName(IMAGE);
                } else if (chainElem instanceof FilterOpChain && ((FilterOpChain) chainElem).firstToken.isKind(OP_GRAY, OP_BLUR, OP_CONVOLVE)){
                    binaryChain.setTypeName(IMAGE);
                } else if(chainElem instanceof IdentChain){ // && IdentChain.getTypeName == IMAGE
                    binaryChain.setTypeName(IMAGE);
                } else if (chainElem.getTypeName() == FILE){
                    binaryChain.setTypeName(NONE);
                } else {
                    throw new TypeCheckException("chainType IMAGE should not have a firstToken of type " + binaryChain.getE1().getTypeName() + " at " + firstToken.getText());
                }
                break;
            case INTEGER:
                if(chainElem instanceof IdentChain ) { // && IdentChain.getTypeName == INTEGER
                    binaryChain.setTypeName(INTEGER);
                }

            default:
                throw new TypeCheckException("Incompatible chain type: " + chainType);
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
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
        Dec dec = symtab.lookup(identX.getFirstToken().getText());
        if (dec == null) {
            throw missingVar(identX.getFirstToken().getText());
        }
        identX.setDec(dec);
        return dec;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        if (intLitExpression.getFirstToken().kind != Scanner.Kind.INT_LIT) {
            throw new TypeCheckException("IntLitExpression should contain an INT_LIT, but instead was: "
                    + intLitExpression.getFirstToken().kind);
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

        TypeName expressionType0 = binaryExpression.getE0().getTypeName();
        TypeName expressionType1 = binaryExpression.getE1().getTypeName();
        Kind opKind = binaryExpression.getOp().kind;


        if (opKind == TIMES) {
            if (expressionType0 == IMAGE || expressionType1 == IMAGE){
                binaryExpression.setTypeName(IMAGE);
            } else if (!(expressionType0 == IMAGE && expressionType1 == IMAGE)) {
                binaryExpression.setTypeName(INTEGER);
            } else {
                throw new TypeCheckException(" Times operator can only be used with Type Image and/or Integer");
            }
            return null;//binaryExpression.getE0() * binaryExpression.getE1();
        }

        if (expressionType0 == expressionType1) {
            switch (opKind) {
                case LT: case GT: case LE: case GE: case OR: case AND:
                    if ((expressionType0 == BOOLEAN) || (expressionType0 == INTEGER) ) { //TODO NEED TO DOUBLE CHECK ASSIGNMENT AND LVALUE
                        binaryExpression.setTypeName(BOOLEAN);
                    } else {
                        throw new TypeCheckException(" Types must be Integer or Boolean for comparative Kinds");
                    }
                    return null;

                case EQUAL:
                    binaryExpression.setTypeName(BOOLEAN);
                    return binaryExpression.getE0() == binaryExpression.getE1();
                case NOTEQUAL:
                    binaryExpression.setTypeName(BOOLEAN);
                    return binaryExpression.getE0() != binaryExpression.getE1();

                case PLUS: case MINUS: case DIV: case MOD:
                    if (expressionType0 == INTEGER || expressionType0 == IMAGE) {
                        binaryExpression.setTypeName(expressionType0);
                    }
                    return null;
            }
        }
        throw new TypeCheckException(" Types must be equal for comparative Ops or Integer or Image for " + expressionType0 + " and " + expressionType1);
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
