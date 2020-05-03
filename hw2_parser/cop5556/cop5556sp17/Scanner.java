/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Author    : O. JosuÃ© Dimanche (JD)                  *
 * UFID      : 92124-6064                              *
 * Class     : COP 3502 Spring 2017                    *
 * Lab 5 	 : Scanner                             	   *
 * Date 	 : Thursday 9 of February of 2017 	(P.D.) *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
	
    /**********************************************************************************************/

	/**
	 * Kind enum
	 */
	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		public String getText() {
			return text;
		}
	
	    static boolean canCombine(String text, Character character) {
	        if (text.isEmpty()) return true;
	        if (character == null) return false;
	        if (isAllDigits(text+character)) return true;
	        if (isIdentifierStart(text.charAt(0))) {
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
	        if (isIdentifierStart(text.charAt(0)) && isIdentifierPart(text.substring(1))) {
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

	    private static boolean isAllDigits(String text) {
	        for (char character : text.toCharArray()) {
	            if (!Character.isDigit(character)) {
	                return false;
	            }
	        }
	        return true;
	    }

	    
	    private static boolean isIdentifierStart(char character) {
	        return Character.isLetter(character) || character == '$' || character == '_';
	    }

	    private static boolean isIdentifierPart(String text) {
	        for (char character : text.toCharArray()) {
	            if (!(isIdentifierStart(character) || Character.isDigit(character))) {
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
	}

    /**********************************************************************************************/
	
	
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
	public static class LinePos {
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
		

	/**********************************************************************************************/
	
	public static class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length; 
		public final LinePos linePos;
        public final String value;
	    public final int line;

        
		//returns the text of this Token
		public String getText() {
			if (kind == Kind.IDENT || kind == Kind.INT_LIT) {
				return value;
	        } else {
	            return kind.getText();
	        }
		}
		
		//returns a LinePos object representing the line and column of this Token
		public LinePos getLinePos(){
			return linePos;
		}

		public Token(Kind kind, String value, int line, int pos, int length) {
	        this.kind = kind;
	        this.linePos = new LinePos(line, pos);
	        this.length = length;
	        this.value = value;
	        this.line = line;
	        this.pos = pos;
	    }

	    Token(Kind kind, int line, int pos, int length) {
	        this(kind, null, line, pos, length);
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
		
	    public int getLength() {
	        return length;
	    }
	}

	public final ArrayList<Token> tokens;
	public final String chars;
	public int tokenNum;

	public Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */

	/****************************************   Scanner   **************************************************/

	public Scanner scan() throws IllegalCharException, IllegalNumberException {
        int tokenPos = 0, tokenLine = 0, curPos = 0;
        char[] charsArray = chars.toCharArray();
        String tempString = "";
        for (int i = 0; i < charsArray.length; i++) {
        	char ch = charsArray[i];
            if (isNewLine(ch)) {
                tokenLine++;
                curPos = 0;
                tokenPos = 0;
                continue;
            } else if ( Character.isWhitespace(ch)) {
                curPos++;
                tokenPos++;
                continue;
            }
            tempString += ch;
            Character nextChar = null;
            if (i < charsArray.length - 1){
                nextChar = charsArray[i + 1];  // read next char first
            }
            if (!Kind.canCombine(tempString, nextChar)) {
                if (nextChar == null || isNewLine(nextChar)
                        || Kind.isOperator(nextChar) || Kind.isSeparator(nextChar)
                        || Kind.isSeparator(ch)) {
                    tokens.add(new Token(Kind.getKind(tempString), tempString, tokenLine, tokenPos, tempString.length()));
                    tempString = "";
                } else {
                    throw new IllegalCharException("Could not process: " + tempString + nextChar);
                }
            }
            curPos++;
            if (tempString.isEmpty()) tokenPos = curPos;
        }
        if (!tempString.isEmpty()) {
            System.err.println("Left over characters: "+tempString);
        }

        tokens.add(new Token(Kind.EOF, "", tokenLine, tokenPos, 0));
        return this;
    }

    private boolean isNewLine(char character) {
        return character == '\n' || character == '\r';
    }


	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size()){
			return null;
		}
		return tokens.get(tokenNum++);
	}
	
	/*
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
	 * Line numbers start counting at 0
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
	
}