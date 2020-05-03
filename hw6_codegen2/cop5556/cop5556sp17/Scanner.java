/**
 * @author JD
 */

package cop5556sp17;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
	/****************************************   Kind   **************************************************/
	/**
	 * Kind enum
	 */
	public enum Kind {
		IDENT(""), INT_LIT(""), NOT("!"),
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
		RBRACE("}"), ARROW("->"), BARARROW("|->"),
		LT("<"), GT(">"), LE("<="), GE(">="), EQUAL("=="), NOTEQUAL("!="), // relOp
		TIMES("*"), DIV("/"), AND("&"),MOD("%"), // strongOp
		PLUS("+"), MINUS("-"), OR("|"), // weakOp
		OP_BLUR("blur"), KW_SCALE("scale"),

		OP_GRAY("gray"), OP_CONVOLVE("convolve"),OP_WIDTH("width"), OP_HEIGHT("height"),
		KW_INTEGER("integer"), KW_BOOLEAN("boolean"),
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"),
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), // BoolExp
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), // Constant Expression
		KW_XLOC("xloc"), KW_YLOC("yloc"),
		ASSIGN("<-"), KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
							EOF("eof"); // End of file token

		final String text;

		Kind(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		private static boolean isAllDigits(String text) {
			for (char character : text.toCharArray()) {
				if (!Character.isDigit(character)) {
					return false;
				}
			}
			return true;
		}

		private static boolean isIdentifierPart(String text) {
			for (char character : text.toCharArray()) {
				if (!(Character.isJavaIdentifierStart(character) || Character.isDigit(character))) {
					return false;
				}
			}
			return true;
		}

		public static boolean isOperator(char character) {
			switch (character) {
				case '&':
				case '=':
				case '!':
				case '<':
				case '>':
				case '+':
				case '-':
				case '*':
				case '/':
				case '%':
					return true;
				default:
					return false;
			}
		}

		public static boolean isSeparator(char character) {
			switch (character) {
				case ' ':
				case ';':
				case ',':
				case '(':
				case ')':
				case '{':
				case '}':
					return true;
				default:
					return false;
			}
		}

		static boolean isFollowSet(String text, Character character) { // Start state and next state
			if (text.isEmpty()) {
				return true; // Epsilon
			}
			if (character == null || text.equals("0")) {
				return false;
			}
			if (isAllDigits(text+character)) {
				return true;
			}
			if (Character.isJavaIdentifierStart(text.charAt(0))) { // check ident list
				if (isIdentifierPart(text.substring(1)+character)) {
					return true;
				}

			}
			for (Kind kind : values()) {
				if (kind.getText().startsWith(text)) {
					if (kind.getText().contains(text+character)) {
						return true;
					}
				}
			}
			return false;
		}

		static Kind getKind(String text) throws IllegalNumberException, IllegalCharException {
			for (Kind kind : values()) {
				if (kind.getText().equals(text)) {
					return kind;
				}
			}

			if (Character.isJavaIdentifierStart(text.charAt(0)) && isIdentifierPart(text.substring(1))) {
				return IDENT;
			}

			if (isAllDigits(text)) {
				try {
					Integer.valueOf(text);
					return INT_LIT;
				} catch (NumberFormatException e) {
					throw new IllegalNumberException("Unable to parse integer from: "+text);
				}
			}

			throw new IllegalCharException("Unable to parse token from: "+text);
		}

	}

	/*************************************** End Kind ***************************************************/
	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	static class IllegalCharException extends Exception {
		IllegalCharException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	static class IllegalNumberException extends Exception {
		IllegalNumberException(String message){
			super(message);
		}
	}

	/*************************************** Line Pos ***************************************************/
	/**
	 * Holds the line and position in the line of a token.
	 */
	public static class LinePos {
		final int line;
		final int posInLine;

		LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}

	/***************************************** Tokens ***************************************************/
	public class Token {
		public final Kind kind;
		final int length;
		final int pos;

		private String value;
		private int[] posLine;

		void setPosLine(Object[] posLine) {
			this.posLine = new int[posLine.length];
			for (int i = 0; i < posLine.length; i++) {
				this.posLine[i] = (int) posLine[i];
			}
		}

		//returns the text of this Token
		public String getText() {
			if (kind == Kind.IDENT || kind == Kind.INT_LIT) {
				return value;
			} else {
				return kind.getText();
			}
		}

		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			int lineNumber = 0;
			for (int i = 0; i < posLine.length; i++) {
				if (posLine[i] < pos) {
					lineNumber = i +1 ;
				} else {
					break;
				}
			}
			return new LinePos(lineNumber, pos);
		}

		Token(Kind kind, int pos, int length) {  /** return signatures to original */
			this.kind = kind;
			this.length = length;
			this.pos = pos;
		}

		void setValue(String value) {
			this.value = value;
		}

		/**
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 *
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			return Integer.valueOf(value);
		}

		int getLength() {
			return length;
		}

		public boolean isKind(Kind kind) {
			return kind == this.kind;
		}

		public boolean isKind(Kind... kinds) {
			for (Kind kind : kinds){
				if (this.kind == kind) {
					return true;
				}
			}
			return false;
		}

		public boolean isZero(char ch, Character nextChar){
			if ( ((Kind.isSeparator(ch)) || ( Kind.isOperator(ch)) || (ch == '|') || (Character.isWhitespace(ch))
			) && (nextChar == '0')){
				return true;
			}
			return false;
		}

		/************************** Assignment 3 copy ************************************/
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}

		private Scanner getOuterType() {
			return Scanner.this;
		}

		/************************** Assignment 3 copied ************************************/

	} // End of Token

	/***************************************** Tokens End ***********************************************/
	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<>();
	}

	/**************************************   Scanner  **************************************************/
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 *
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0,  curPos = 0;

		char[] charsArray = chars.toCharArray();
		String tempString = "";
		List<Integer> posLines = new ArrayList<>();

		boolean inComment = false;  // implementing comments

		for (int i = 0; i < charsArray.length; i++) {

			char ch = charsArray[i];
			Character nextChar =' ';

			if (i < charsArray.length - 1){
				nextChar = charsArray[i + 1];  // read next char first
			}
			if ((ch == '/') && (nextChar == '*')){
				inComment = true;
			}
			if ((ch == '*') && (nextChar == '/')){
				i++;
				curPos+=2;
				pos+=2;
				inComment = false;
				continue;
			}
			if (ch == '\n') { //TODO is \r\n considered line++ or line+=2
				posLines.add(curPos);
				curPos++;
				pos++;
				continue;
			} else if ( Character.isWhitespace(ch)) {
				curPos++;
				pos++;
				continue;
			}

			if (!inComment){
				tempString += ch;
				if (charsArray[i] == '|') {
					if (nextChar == '-' &&
							(i + 2) < charsArray.length && charsArray[i + 2] == '>') {
						Token token = new Token(Kind.getKind("|->"), pos, "|->".length());
						token.setValue("|->");
						token.setPosLine(posLines.toArray());
						tokens.add(token);
						tempString = "";
						i += 2;
						curPos+=2;
					} else {
						Token token = new Token(Kind.getKind("|"), pos, "|".length());
						token.setValue("|");
						token.setPosLine(posLines.toArray());
						tokens.add(token);
						tempString = "";
					}
				} else if (!Kind.isFollowSet(tempString, nextChar)) {
					if (isNewLine(nextChar) || nextChar == '|' || tempString.equals("0")
							|| Kind.isOperator(nextChar) || Kind.isSeparator(nextChar)
							|| Kind.isSeparator(ch) || Kind.isSeparator(charsArray[i]) || Kind.isOperator(charsArray[i])) {
						Token token = new Token(Kind.getKind(tempString), pos, tempString.length());
						token.setValue(tempString);
						token.setPosLine(posLines.toArray());
						tokens.add(token);
						tempString = "";
					} else {
						throw new IllegalCharException("Could not process: " + tempString + nextChar);
					}
				}
			}
			curPos++;
			if (tempString.isEmpty()) pos = curPos;
		}
		if (!tempString.isEmpty()) {
			System.err.println("Left over characters: "+tempString);
		}
		if (!inComment) {
			Token token = new Token(Kind.EOF, pos, 0);
			token.setValue("");
			token.setPosLine(posLines.toArray());
			tokens.add(token);
		} else {
			throw new IllegalCharException("Unclosed Comment at EOF" + tempString);
		}
		return this;
	}

	/*************************************  End of Scanner  *********************************************/
	private boolean isNewLine(char character) {
		return character == '\n' || character == '\r';
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/**
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..
	 */
	Token nextToken() {
		if (tokenNum >= tokens.size()){
			return null;
		}
		return tokens.get(tokenNum++);
	}

	/**
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size()){
			return null;
		}
		return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the
	 * given token.
	 *
	 * Line numbers start counting at 0 ????
	 *
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		for (Token token : tokens) {
			if (token == t) {
				return token.getLinePos();
			}
		}
		return null;
	}

	/*************************************   End of File  ***********************************************/
}