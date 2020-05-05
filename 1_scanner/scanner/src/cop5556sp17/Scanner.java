package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
    /**
     * Kind enum
     */

    public static boolean inIdent;
    public boolean inComment;
    final ArrayList<Token> tokens;
    final String chars;
    int tokenNum;


    public enum Kind {
        AND("&"),
        ARROW("->"),

        ASSIGN("<-"), BARARROW("|->"), COMMA(","), COMMENT("*/"), DIV("/"),
        EOF("eof"), EQUAL("=="), GE(">="),
        GT(">"), IDENT(""),
        INT_LIT(""), KW_BOOLEAN("boolean"), KW_FALSE("false"),

        KW_FILE("file"), KW_FRAME("frame"), KW_HIDE("hide"), KW_IF("if"),
        KW_IMAGE("image"), KW_INTEGER("integer"),

        KW_MOVE("move"), KW_SCALE("scale"), KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), KW_SHOW("show"),
        KW_TRUE("true"),
        KW_URL("url"), KW_WHILE("while"), KW_XLOC("xloc"), KW_YLOC("yloc"), LBRACE("{"),

        LE("<="), LPAREN("("), LT("<"), MINUS("-"),

        MOD("%"), NOT("!"), NOTEQUAL("!="), OP_BLUR("blur"), OP_CONVOLVE("convolve"),
        OP_GRAY("gray"), OP_HEIGHT("height"), OP_SLEEP("sleep"),

        OP_WIDTH("width"), OR("|"), PLUS("+"), RBRACE("}"),
        RPAREN(")"), SEMI(";"), TIMES("*"), UNCOMMENT("/*");

        Kind(String text) {
            this.text = text;
        }

        final String text;

        String getText() {
            return text;
        }

        public static Kind getKind(String text) {
            switch (text) {
                case "false":
                    return KW_FALSE;
                case "true":
                    return KW_TRUE;
                // frame_op_keyword ∷= xloc | yloc | hide | show | move
                case "xloc":
                    return KW_XLOC;
                case "yloc":
                    return KW_YLOC;
                case "hide":
                    return KW_HIDE;
                case "show":
                    return KW_SHOW;
                case "move":
                    return KW_MOVE;
                // image_op_keyword ∷= width | height
                case "width":
                    return OP_WIDTH;
                case "height":
                    return OP_HEIGHT;
                // filter_op_keyword ∷= gray | convolve | blur | scale
                case "gray":
                    return OP_GRAY;
                case "convolve":
                    return OP_CONVOLVE;
                case "blur":
                    return OP_BLUR;
                case "scale":
                    return KW_SCALE;
                // keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth
                case "integer":
                    return KW_INTEGER;
                case "boolean":
                    return KW_BOOLEAN;
                case "image":
                    return KW_IMAGE;
                case "url":
                    return KW_URL;
                case "file":
                    return KW_FILE;
                case "frame":
                    return KW_FRAME;
                case "while":
                    return KW_WHILE;
                case "if":
                    return KW_IF;
                case "sleep":
                    return OP_SLEEP;
                case "screenheight":
                    return KW_SCREENHEIGHT;
                case "screenwidth":
                    return KW_SCREENWIDTH;
                // separator ::=  ;  | ,  |  (  |  )  | { | }
                case ";":
                    return SEMI;
                case ",":
                    return COMMA;
                case "(":
                    return LPAREN;
                case ")":
                    return RPAREN;
                case "{":
                    return LBRACE;
                case "}":
                    return RBRACE;
                // operator ::=	|  | &  |  ==  | !=  | < |  > | <= | >= | +  |  -  |  *   |  /   |  % | !  | -> |  |-> | <-
                case "|":
                    return OR;
                case "&":
                    return AND;
                case "==":
                    return EQUAL;
                case "!=":
                    return NOTEQUAL;
                case "<":
                    return LT;
                case ">":
                    return GT;
                case "<=":
                    return LE;
                case ">=":
                    return GE;
                case "+":
                    return PLUS;
                case "-":
                    return MINUS;
                case "*":
                    return TIMES;
                case "/":
                    return DIV;
                case "%":
                    return MOD;
                case "!":
                    return NOT;
                case "->":
                    return ARROW;
                case "|->":
                    return BARARROW;
                case "<-":
                    return ASSIGN;

                default:
                    return null;
            }
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

    public static class Token {
        public final Kind kind;
        public final int pos;  //position in input array
        public final int posInLine;  //position in input array
        public final LinePos linePos;  //position in input array
        public final int line;  //position in input array
        public final int length;
        public final String text;
        private final int intVal;

        //returns the text of this Token
        public String getText() {
            return this.text;
        }

        //returns a LinePos object representing the line and column of this Token
        LinePos getLinePos(){
            return this.linePos;
        }


        Token(Kind kind, int pos, int length, int line, int posInLine, String text) {
            this (kind, pos, length, line, posInLine, text, 0);
        }

        Token(Kind kind, int pos, int length, int line, int posInLine, String text, int value) {
            this.kind = kind;
            this.pos = pos - (text.length() - 1);
            this.length = length;
            this.line = line;
            this.posInLine = posInLine - (text.length() - 1);
            this.text = text;
            this.linePos = new LinePos(line, posInLine);
            this.intVal = value;
        }



        /**
         * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
         * Note that the validity of the input should have been checked when the Token was created.
         * So the exception should never be thrown.
         * @return  int value of this token, which should represent an INT_LIT
         * @throws NumberFormatException
         */
        public int intVal() throws NumberFormatException{
            return this.intVal;
        }
    }

    Scanner(String chars) {
        this.chars = chars;
        tokens = new ArrayList<Token>();
    }

    public static Boolean isIdentStartOnly(char c){
        if (Character.isUpperCase(c) || (c == '$') || (c == '_')){
            inIdent = true;
            return true;
        }
        return false;
    }

    /**
     * Initializes Scanner object by traversing chars and adding tokens to tokens list.
     *
     * @return this scanner
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    public Scanner scan() throws IllegalCharException, IllegalNumberException {
        int charsLength = this.chars.length();
        char[] charsArray = this.chars.toCharArray();
        char curChar, nextChar;
        int pos = 0;
        int posInLine = 0, line = 0;
        StringBuilder text = new StringBuilder();

        for (pos = 0; pos < charsLength; ++pos) {
            curChar = charsArray[pos];

            if ((curChar == '\n') || (curChar == '\r')) {
                line += 1;
                posInLine = 0;
            } else if (curChar == ' '){
                if (text.length() > 0){
                    tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    text = new StringBuilder();
                }
                posInLine += 1;
            } else if ((curChar == ';') || (curChar == ',') || (curChar == '(') || (curChar == ')') || (curChar == '{')
                    || (curChar == '}') || (curChar == '&') || (curChar == '+') || (curChar == '%')) {
                text.append(curChar);
                tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                text = new StringBuilder();
                posInLine += 1;

            } else if (pos + 1 < charsLength){
                nextChar = charsArray[pos + 1];

                if (curChar == '-'){
                    text.append(curChar);
                    if (nextChar != '>'){
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    } else {
                        text.append(nextChar);
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        pos += 1;
                    }
                    text = new StringBuilder();
                    posInLine += 1;
                }
                else if (Character.isLowerCase(curChar) && (text.length() == 0)){
                    text.append(curChar);
                    while ((pos+1 <= charsLength-1) && (Character.isDigit(charsArray[pos+1]) || (Character.isLowerCase(charsArray[pos+1])))){
//                        System.out.println(pos);
                        pos++;
                        posInLine++;
                        curChar = charsArray[pos];
                        text.append(curChar);
                    }
                    if (Kind.getKind(text.toString()) != null){
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    } else {
                        tokens.add(new Token(Kind.IDENT, pos, text.length(), line, posInLine, text.toString()));
                    }
                    text = new StringBuilder();
                    posInLine++;
                }
                else if (isIdentStartOnly(curChar) && (text.length() == 0)){
                    text.append(curChar);
                    while ((pos+1 <= charsLength-1) && (Character.isDigit(charsArray[pos+1]) || (Character.isLowerCase(charsArray[pos+1])))){
//                        System.out.println(pos);
                        pos++;
                        posInLine++;
                        curChar = charsArray[pos];
                        text.append(curChar);
                    }
                    tokens.add(new Token(Kind.IDENT, pos, text.length(), line, posInLine, text.toString()));
                    text = new StringBuilder();
                    posInLine++;
                }

                else if (Character.isDigit(curChar)){
                    text.append(curChar);
                    while ((pos+1 <= charsLength-1) && (Character.isDigit(charsArray[pos+1]))){
                        posInLine++;
                        curChar = charsArray[pos];
                        text.append(curChar);
                        pos++;
                    }
                    try {
                        int value = Integer.parseInt(text.toString());
                        tokens.add(new Token(Kind.INT_LIT, pos, text.length(), line, posInLine, text.toString(), value));
                        text = new StringBuilder();
                    } catch (NumberFormatException e){
                        throw new IllegalNumberException("number provided is larger than we can work with");
                    }
                    posInLine++;
                }
                else if (curChar == '|'){
                    text.append(curChar);
                    if (nextChar != '-'){
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    } else {
                        if (pos + 2 >= charsLength){
                            System.out.println(pos);
                            System.out.println(charsLength);
                            throw new IllegalCharException("This is an out of index exception");
                        }

                        text.append(nextChar);
                        text.append(charsArray[pos + 2]);
                        posInLine += 2;
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        pos += 2;
                    }
                    text = new StringBuilder();
                    posInLine += 1;
                }
                else if ((curChar == '>') || curChar == '!') {
                    text.append(curChar);
                    if (nextChar != '='){
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    } else {
                        text.append(nextChar);
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        pos += 1;
                    }
                   text = new StringBuilder();
                   posInLine += 1;
                } else if (curChar == '<') {
                    text.append(curChar);
                   if (nextChar == '='){
                        text.append(nextChar);
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        pos += 1;
                    } else if (nextChar == '-'){
                        text.append(nextChar);
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        pos += 1;
                    } else {
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                    }
                   text = new StringBuilder();
                   posInLine += 1;
                }  else if ((curChar == '/') || (curChar == '*')) {
                    // check for comments
                    if (nextChar == '*') {
                        text = new StringBuilder("/*");
                        posInLine += 1;
                        tokens.add(new Token(Kind.COMMENT, pos, text.length(), line, posInLine, text.toString()));
                        inComment = true;
                        pos += 1;
                        text = new StringBuilder();
                    } else if ((nextChar == '/') && inComment) {
                        text = new StringBuilder("*/");
                        posInLine += 1;
                        tokens.add(new Token(Kind.UNCOMMENT, pos, text.length(), line, posInLine, text.toString()));
                        inComment = false;
                        pos += 1;
                        text = new StringBuilder();
                    } else {
                        text.append(curChar);
                        tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                        text = new StringBuilder();
                    }
                    posInLine += 1;
                }
            } else if (Character.isLowerCase(curChar)){
                text.append(curChar);
                tokens.add(new Token(Kind.getKind(text.toString()), pos, text.length(), line, posInLine, text.toString()));
                posInLine += 1;
            }
        }

        if (inComment){
            throw new IllegalCharException("Unclosed Comments at line[" + line +"] column[" + posInLine + "]");
        }
        tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine, Kind.EOF.text));
        return this;
    }


    /*
     * Return the next token in the token list and update the state so that
     * the next call will return the Token..
     */
    public Token nextToken() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum++);
    }

    public Token prevToken() {
        if (tokenNum >= 1)
            return null;
        return tokens.get(tokenNum-1);
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