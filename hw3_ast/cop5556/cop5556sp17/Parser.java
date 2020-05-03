/**
 * @Author jodth07
 */
package cop5556sp17;

import cop5556sp17.AST.*;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cop5556sp17.Scanner.Kind.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 * @SuppressWarnings("serial")
	 */
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/***************************************** Static Kinds Array ***************************************/

	static private Kind[] relOpKinds = {LT, LE, GT, GE, EQUAL, NOTEQUAL};
	static private Kind[] arrowOpKinds = {ARROW, BARARROW};
	static private Kind[] weakOpKinds = {PLUS, MINUS, OR};
	static private Kind[] strongOpKinds = {DIV, TIMES, AND, MOD};
	static private Kind[] statementKinds = {OP_SLEEP, KW_WHILE, KW_IF, IDENT};
	static private Kind[] filterOpKinds = {OP_BLUR, OP_GRAY, OP_CONVOLVE};
	static private Kind[] frameOpKinds = {KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC};
	static private Kind[] imageOpKinds = {OP_WIDTH, OP_HEIGHT, KW_SCALE};
	static private Kind[] paramDecKinds = {KW_URL, KW_FILE, KW_INTEGER, KW_BOOLEAN};
	static private Kind[] decKinds = {KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_FRAME};
	static {
		List<Kind> statementKindsList = new ArrayList<>();
		Collections.addAll(statementKindsList, statementKinds);
		Collections.addAll(statementKindsList, filterOpKinds);
		Collections.addAll(statementKindsList, frameOpKinds);
		Collections.addAll(statementKindsList, imageOpKinds);
		statementKinds = statementKindsList.toArray(statementKinds);
	}

	/************************************** Static Kinds Array End **************************************/

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 *
	 * @throws SyntaxException
	 */
	public ASTNode parse() throws SyntaxException {
		Program program = program();
		matchEOF();
		return program;
	}

	/***********************************  Program, Block, Chains, && arg  *******************************/

	/**
	 * program ::  IDENT block
	 * program ::= IDENT param_dec* block
	 * @throws SyntaxException
	 *
	 * * Program(Token firstToken, ArrayList<ParamDec> paramList, Block b) *
	 * * ParamDec(Token firstToken, Token ident) *
	 */
	Program program() throws SyntaxException {
		Scanner.Token firstToken = match(IDENT);
		ArrayList<ParamDec> paramList = new ArrayList<>();
		if (t.isKind(paramDecKinds)) {
			paramList.add(new ParamDec(paramDec(), firstToken));
			while (t.isKind(COMMA)) {
				consume();
				paramList.add(new ParamDec(paramDec(), firstToken)); // What does paramdec() returns
			}
		}
		return new Program(firstToken, paramList, block());
	}

	/**
	 * block ::= { ( dec | statement) * }
	 * @throws SyntaxException
	 */
	Block block() throws SyntaxException {
		Scanner.Token firstToken = match(LBRACE);
		ArrayList<Dec> decList = new ArrayList<>();
		ArrayList<Statement> statementList = new ArrayList<>();
		boolean isDec = t.isKind(decKinds), isStatement = t.isKind(statementKinds);
		do {
			if (isDec) {
				decList.add(new Dec(dec(), firstToken));
			} else if (isStatement) {
				statementList.add(statement());
			}
			isDec = t.isKind(decKinds);
			isStatement = t.isKind(statementKinds);
		} while (isDec || isStatement);
		match(RBRACE);
		return new Block(firstToken, decList, statementList);
	}

	/**
	 * chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
	 * @throws SyntaxException
	 *
	 * Chain(Token firstToken)
	 */
	Chain chain() throws SyntaxException {
		ChainElem elem = chainElem();
		Scanner.Token arrowOp = arrowOp();
		ChainElem secondElem = chainElem();
		Chain binaryElem = new BinaryChain(elem.firstToken, elem, arrowOp, secondElem);
		while(t.isKind(arrowOpKinds)){
			arrowOp = arrowOp();
			ChainElem thirdElem = chainElem();
			binaryElem = new BinaryChain(binaryElem.firstToken, binaryElem, arrowOp, thirdElem);
		}
		return binaryElem;
	}

	/**
	 * chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
	 * @throws SyntaxException
	 *
	 * ChainElem(Token firstToken)
	 */
	ChainElem chainElem() throws SyntaxException {
		if (t.isKind(IDENT)){
			return new IdentChain(match(IDENT));
		} else if (t.isKind(imageOpKinds)){
			Scanner.Token firstToken = imageOp(); //
			Tuple arg = arg();
			return new ImageOpChain(firstToken, arg);
		} else if (t.isKind(filterOpKinds)){
			Scanner.Token firstToken = filterOp(); //
			Tuple arg = arg();
			return new FilterOpChain(firstToken, arg);
		} else if (t.isKind(frameOpKinds)) {
			Scanner.Token firstToken = frameOp(); //
			Tuple arg = arg();
			return new FrameOpChain(firstToken, arg);
		} else {
			throw new SyntaxException(" Unexpected ChainElement Kind " + t.kind + " at position " + t.pos);
		}
	}

	/**
	 * arg ::= ε | ( expression ( ,expression)* )
	 * @throws SyntaxException
	 * Tuple arg() --> Tuple (Token firstToken, List<Expression> argList)
	 */
	Tuple arg() throws SyntaxException {
		List<Expression> expressions = new ArrayList<>();
		Tuple arg = new Tuple(t, expressions) ;
		if(t.isKind(LPAREN)) {
			consume();
			expressions.add(expression());
			while (t.isKind(COMMA)) {
				consume();
				expressions.add(expression());
			}
			match(RPAREN);
		}
		return arg;
	}

	/***************************************** Statements ***********************************************/

	/**
	 * statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
	 * @throws SyntaxException
	 *
	 * Statement(Token firstToken)
	 */
	Statement statement() throws SyntaxException {
		if (t.isKind(OP_SLEEP)){
			Scanner.Token firstToken = match(OP_SLEEP);
			Expression expression = expression();
			match(SEMI);
			return new SleepStatement(firstToken,expression);
		} else if (t.isKind(KW_IF)) {
			return ifStatement();
		} else if (t.isKind(KW_WHILE)) {
			return whileStatement();
		} else if (t.isKind(IDENT) && scanner.peek().isKind(ASSIGN)) {
			AssignmentStatement assign = assign();
			match(SEMI);
			return assign;
		} else {
			Chain chain = chain();
			match(SEMI);
			return chain;
		}
	}

	IfStatement ifStatement() throws  SyntaxException {
		Scanner.Token ifToken = match(KW_IF);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		Block block = block();
		return new IfStatement(ifToken, expression, block);
	}

	WhileStatement whileStatement() throws SyntaxException {
		Scanner.Token whileToken = match(KW_WHILE);
		match(LPAREN);
		Expression expression = expression();
		match(RPAREN);
		Block block = block();
		return new WhileStatement(whileToken, expression, block);
	}

	AssignmentStatement assign() throws SyntaxException {
		Scanner.Token firstToken = match(IDENT);
		IdentLValue identLValue = new IdentLValue(firstToken);
		match(ASSIGN);
		Expression expression = expression();
		return new AssignmentStatement(firstToken,identLValue, expression);
	}

	/***************************************** Expressions **********************************************/

	/**
	 * expression ∷= term ( relOp term)*
	 * @throws SyntaxException
	 */
	Expression expression() throws SyntaxException {
		Expression term = term();
		Expression expression = null;
		while (t.isKind(relOpKinds)) {
			Scanner.Token relOp = relOp();
			Expression secondTerm = term();
			if (expression == null) {
				expression = new BinaryExpression(term.firstToken, term,
						relOp, secondTerm);
			} else {
				expression = new BinaryExpression(expression.firstToken, expression,
						relOp, secondTerm);
			}
		}
		return expression != null ? expression : term;
	}

	/**
	 * term ∷= elem ( weakOp elem)*
	 * @throws SyntaxException
	 */
	Expression term() throws SyntaxException {
		Expression elem = elem();
		Expression expression = null;
		while (t.isKind(weakOpKinds)) {
			Scanner.Token weakOp = t;
			weakOp();
			Expression secondElem = elem();
			if (expression == null) {
				expression = new BinaryExpression(elem.firstToken, elem,
						weakOp, secondElem);
			} else {
				expression = new BinaryExpression(expression.firstToken, expression,
						weakOp, secondElem);
			}
		}
		return expression != null ? expression : elem;
	}

	/**
	 *  elem ∷= factor ( strongOp factor)*
	 * @throws SyntaxException
	 */
	Expression elem() throws SyntaxException {
		Expression factor = factor();
		Expression expression = null;
		while (t.isKind(strongOpKinds)) {
			Scanner.Token strongOp = t;
			strongOp();
			Expression secondFactor = factor();
			if (expression == null) {
				expression = new BinaryExpression(factor.firstToken, factor,
						strongOp, secondFactor);
			} else {
				expression = new BinaryExpression(expression.firstToken, expression,
						strongOp, secondFactor);
			}
		}
		return expression != null ? expression : factor;
	}

	/**
	 * factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
	 * @throws SyntaxException
	 */
	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
			case IDENT: {
				return new IdentExpression(match(IDENT));
			}
			case INT_LIT: {
				return new IntLitExpression(match(INT_LIT));
			}
			case KW_TRUE:  case KW_FALSE: {
				return new BooleanLitExpression(match(KW_TRUE, KW_FALSE));
			}
			case KW_SCREENWIDTH:  case KW_SCREENHEIGHT: {
				return new ConstantExpression(match(KW_SCREENWIDTH, KW_SCREENHEIGHT));
			}
			case LPAREN: {
				consume();
				Expression expression = expression();
				match(RPAREN);
				return expression;
			}
			default:
				throw new SyntaxException("Unexpected factor kind "+t.kind+" at position "+t.pos);
		}
	}

	/********************************************* Token OpChains and decs ****************************************/

	/**
	 * paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)
	 * @throws SyntaxException
	 *
	 * ParamDec(Token firstToken, Token ident);
	 */
	Scanner.Token paramDec() throws SyntaxException {
		return match(paramDecKinds);
	}

	/**
	 * dec ::= ( KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)
	 * @throws SyntaxException
	 */
	Scanner.Token dec() throws SyntaxException {
		return match(decKinds);
	}

	/**
	 * filterOp ::= OP_BLUR |OP_GRAY | OP_CONVOLVE
	 * @throws SyntaxException
	 *
	 * FilterOpChain(Token firstToken, Tuple arg)
	 */
	Scanner.Token filterOp() throws SyntaxException {
		return match(filterOpKinds);
	}

	/**
	 * frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC |KW_YLOC
	 * @throws SyntaxException
	 *
	 * FrameOpChain(Token firstToken, Tuple arg)
	 */
	Scanner.Token frameOp() throws SyntaxException {
		return match(frameOpKinds);
	}

	/**
	 *  imageOp ::= OP_WIDTH |OP_HEIGHT | KW_SCALE
	 * @throws SyntaxException
	 *
	 * 	ImageOpChain(Token firstToken, Tuple arg)
	 */
	Scanner.Token imageOp() throws SyntaxException {
		return match(imageOpKinds);
	}

	/********************************* relOp, weakOp, & strongOp ****************************************/

	private Scanner.Token arrowOp() throws SyntaxException {
		return match(arrowOpKinds);
	}

	private Scanner.Token relOp() throws SyntaxException {
		return match(relOpKinds);
	}

	private Scanner.Token weakOp() throws SyntaxException {
		return match(weakOpKinds);
	}

	private Scanner.Token strongOp() throws SyntaxException {
		return  match(strongOpKinds);
	}

	/********************************* Matching and Consuming *******************************************/
	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 *
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 *
	 * Precondition: kind != EOF
	 *
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + " expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 *
	 * * Precondition: for all given kinds, kind != EOF  ???? is it already met, or needs to be implemented? ??????
	 *
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		for (Kind kind : kinds){
			if(t.isKind(kind)){
				return consume();
			}
		}
		throw new SyntaxException("saw " + t.kind + " expected " + kinds);
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * Precondition: t.kind != EOF*
	 * @return*
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

	/******************************** End of File  ******************************************************/
}
