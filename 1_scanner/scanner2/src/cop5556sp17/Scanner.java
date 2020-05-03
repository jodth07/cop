package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""),
		KW_INTEGER("integer"), KW_BOOLEAN("boolean"), KW_IMAGE("image"),
		KW_FRAME("frame"), KW_URL("url"), KW_FILE("file"),
		KW_WHILE("while"), KW_IF("if"),
		KW_TRUE("true"), KW_FALSE("false"),
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		final String text;

		Kind(String text) {
			this.text = text;
		}

		String getText() {
			return text;
		}

		public static Kind getKind(String text) {
			System.out.println(text);
			for (Kind kind : Kind.values()) {
				if (kind.text.equalsIgnoreCase(text)) {
					return kind;
				}
			}
			return null;
		}

	}

	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
		public IllegalNumberException(String message){
			super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}


	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;
		private final String token;
		private final int line;
		private final int posInLine;



		//returns the text of this Token
		public String getText() {
			return this.token;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			return new LinePos(line, posInLine);
		}

		Token(Kind kind, int pos, int length) {
			this(kind, pos, length, "", 0);
		}

		Token(Kind kind, int pos, int length, String token) {
			this(kind, pos, length, token, 0);
		}

		Token(Kind kind, int pos, int length, String token, int line) {
			this(kind, pos, length, token, line, 0);
		}

		Token(Kind kind, int pos, int length, String token, int line, int posInLine) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.token = token;
			this.line = line;
			this.posInLine = posInLine;
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
			//TODO IMPLEMENT THIS
			return 0;
		}
		
	}


	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}


	private final String validChars = ";%>< (){}&,$+_";
	private final String singleCharKeys = ";|%*(){}&,$!/+_\\t";


	private boolean isValidChar(char ch) throws IllegalCharException{
		if (Character.isAlphabetic(ch) || Character.isDigit(ch) || validChars.contains(Character.toString(ch))){
			return true;
		} else {
			throw new IllegalCharException ("'"+ ch + "' is is not a valid character" + "LinePos [line=" + curPos + ", posInLine=" + lineNum + "]" );
		}
	}
	public int lineNum = 0;
	public int curPos = 0;

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 *
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0, curPos = 0, tokenStartPos = 0, tokenEndPos = 0, tokenLength = 0;
		lineNum = 0;

		String token = "";
		char prevChar, curChar, nextChar;
		int charsLength = chars.length();

		//TODO IMPLEMENT THIS!!!!
		for (int i = 0; i < charsLength; ++i){
			curChar = chars.charAt(i);

			if (Character.isSpaceChar(curChar)){
				if (token.length() > 0){
					tokens.add(new Token(Kind.getKind(token), pos, token.length(), token, lineNum, curPos));
					System.out.println("Added new Token -191- '" + token + "' of length " + token.length() + "to token's array: ");
					token = "";
				}
				pos += 1;
				curPos +=1;
				continue;
			} else if ((curChar == '\n') || (curChar == '\r') ){
				pos += 1;
				lineNum +=1;
				i += 1;
				curPos = 0;
			} else {
				if (i < charsLength - 1){
					nextChar = chars.charAt(i + 1);

					if (Character.isSpaceChar(nextChar)){
						token += curChar;

						tokens.add(new Token(Kind.getKind(token), pos, token.length(), token, lineNum, curPos));
						System.out.println("Added new Token -210- '" + token + "' of length " + token.length() + "to token's array: ");
						token = "";
					} else {
						token += curChar;
					}
				} else {
					token += curChar;
				}
			}

			// Final check for remaining token if any
			if (i == charsLength - 1){
				if (token.length() > 0){
					tokens.add(new Token(Kind.getKind(token), pos, token.length(), token, lineNum, curPos));
					System.out.println("Added new Token 219'" + token + "' of length " + token.length() + "to token's array: ");
				}
			}
		}

		tokens.add(new Token(Kind.EOF, pos,0));
		return this;  
	}

	public static void RemoveComment(){

	}


	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum+1);		
	}


	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		return t.getLinePos();
	}

}
