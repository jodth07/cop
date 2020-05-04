package cop5556sp17;

import java.util.ArrayList;

public class Scanner {
    /**
     * Kind enum
     */

    public static enum Kind {
        IDENT(""), INT_LIT(""),

        KW_FILE("file"), KW_FRAME("frame"), KW_HIDE("hide"), KW_IMAGE("image"), KW_MOVE("move"),
        KW_SCALE("scale"), KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"),
        KW_SHOW("show"), OP_SLEEP("sleep"), KW_URL("url"), KW_XLOC("xloc"), KW_YLOC("yloc"),

        KW_BOOLEAN("boolean"), KW_INTEGER("integer"), KW_TRUE("true"), KW_FALSE("false"),
        KW_IF("if"), KW_WHILE("while"),

        SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
        RBRACE("}"),
        AND("&"), TIMES("*"), DIV("/"), MOD("%"), PLUS("+"),


        NOT("!"), NOTEQUAL("!="), LT("<"), GT(">"),

        OR("|"), MINUS("-"), ARROW("->"), BARARROW("|->"), ASSIGN("<-"),
        EQUAL("=="),   LE("<="), GE(">="),


        OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), OP_WIDTH("width"),
        OP_HEIGHT("height"), EOF("eof"), COMMENT("*/"), UNCOMMENT("/*");


        Kind(String text) {
            this.text = text;
        }

        final String text;

        String getText() {
            return text;
        }

        public static Kind getKind(String text){
            switch (text){
                case "false" : return KW_FALSE;
                case "true" : return KW_TRUE;
                // frame_op_keyword ∷= xloc | yloc | hide | show | move
                case "xloc" : return KW_XLOC;
                case "yloc" : return KW_YLOC;
                case "hide" : return KW_HIDE;
                case "show" : return KW_SHOW;
                case "move" : return KW_MOVE;
                // image_op_keyword ∷= width | height
                case "width" : return OP_WIDTH;
                case "height" : return OP_HEIGHT;
                // filter_op_keyword ∷= gray | convolve | blur | scale
                case "gray" : return OP_GRAY;
                case "convolve" : return OP_CONVOLVE;
                case "blur" : return OP_BLUR;
                case "scale" : return KW_SCALE;
                // keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth
                case "integer" : return KW_INTEGER;
                case "boolean" : return KW_BOOLEAN;
                case "image" : return KW_IMAGE;
                case "url" : return KW_URL;
                case "file" : return KW_FILE;
                case "frame" : return KW_FRAME;
                case "while" : return KW_WHILE;
                case "if" : return KW_IF;
                case "sleep" : return OP_SLEEP;
                case "screenheight" : return KW_SCREENHEIGHT;
                case "screenwidth" : return KW_SCREENWIDTH;
                // separator ::=  ;  | ,  |  (  |  )  | { | }
                case ";" : return SEMI;
                case "," : return COMMA;
                case "(" : return LPAREN;
                case ")" : return RPAREN;
                case "{" : return LBRACE;
                case "}" : return RBRACE;
                // operator ::=	|  | &  |  ==  | !=  | < |  > | <= | >= | +  |  -  |  *   |  /   |  % | !  | -> |  |-> | <-
                case "|" : return OR;
                case "&" : return AND;
                case "==" : return EQUAL;
                case "!=" : return NOTEQUAL;
                case "<" : return LT;
                case ">" : return GT;
                case "<=" : return LE;
                case ">=" : return GE;
                case "+" : return PLUS;
                case "-" : return MINUS;
                case "*" : return TIMES;
                case "/" : return DIV;
                case "%" : return MOD;
                case "!" : return NOT;
                case "->" : return ARROW;
                case "|->" : return BARARROW;
                case "<-" : return ASSIGN;

                default: return null;
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


        //returns the text of this Token
        public String getText() {
            return this.text;
        }

        //returns a LinePos object representing the line and column of this Token
        LinePos getLinePos(){
            return this.linePos;
        }

        Token(Kind kind, int pos, int length, int line, int posInLine, String text) {
            this.kind = kind;
            this.pos = pos - (text.length() - 1);
            this.length = length;
            this.line = line;
            this.posInLine = posInLine - (text.length() - 1);
            this.text = text;
            this.linePos = new LinePos(line, posInLine);
        }

        /**
         * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
         * Note that the validity of the input should have been checked when the Token was created.
         * So the exception should never be thrown.
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


    public boolean inComment = false;

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
        char curChar, nextChar, prevChar;
        int pos = 0;
        int posInLine = 0, line = 0;
        String text = "";

        for (pos = 0; pos < charsLength; ++pos) {
            curChar = charsArray[pos];

            if (pos > 0) {
                prevChar = charsArray[pos - 1];
            }

            if ((curChar == '\n') || (curChar == '\r')) {
                line += 1;
                posInLine = 0;
            } else if (curChar == ' '){
                posInLine += 1;
            } else if ((curChar == ';') || (curChar == ',') || (curChar == '(') || (curChar == ')') || (curChar == '{')
                    || (curChar == '}') || (curChar == '|') || (curChar == '&') || (curChar == '+') || (curChar == '%')){
                text += curChar;
                tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                text = "";
                posInLine += 1;
            } else if (pos + 1 < charsLength){
                nextChar = charsArray[pos + 1];

                if (curChar == '!'){
                    text += curChar;
                    if (nextChar != '='){
                        tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                        text = "";
                    } else if (nextChar == '=') {
                        text += nextChar;
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                        pos += 1;
                        text = "";
                    }
                    posInLine += 1;
                } else if ((curChar == '<') || (curChar == '>')) {
                    text += curChar;
                    if (nextChar != '='){
                        tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                        text = "";
                    } else if (nextChar == '='){
                        text += nextChar;
                        posInLine += 1;
                        tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                        pos += 1;
                        text = "";
                    }
                    posInLine += 1;
                }  else if ((curChar == '/') || (curChar == '*')) {
                    // check for comments
                    if ((curChar == '/') && (nextChar == '*')) {
                        text = "/*";
                        posInLine += 1;
                        tokens.add(new Token(Kind.COMMENT, pos, text.length(), line, posInLine, text));
                        inComment = true;
                        pos += 1;
                        text = "";
                    } else if ((curChar == '*') && (nextChar == '/') && inComment) {
                        text = "*/";
                        posInLine += 1;
                        tokens.add(new Token(Kind.UNCOMMENT, pos, text.length(), line, posInLine, text));
                        inComment = false;
                        pos += 1;
                        text = "";
                    } else {
                        text += curChar;
                        tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                        text = "";
                    }
                    posInLine += 1;
                } else if ((Character.isAlphabetic(curChar) && (nextChar == ' '))){
                    text += curChar;
                    tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                    text = "";
                    pos += 1;
                    posInLine += 2;
                } else if ((Character.isAlphabetic(curChar) && Character.isAlphabetic(nextChar))){
                    text += curChar;
                    posInLine += 1;
                }
            } else if (Character.isAlphabetic(curChar)){
                text += curChar;
                tokens.add(new Token(Kind.getKind(text), pos, text.length(), line, posInLine, text));
                posInLine += 1;
            }

        }



        //TODO IMPLEMENT THIS!!!!
        if (inComment){
            throw new IllegalCharException("Unclosed Comments at line[" + line +"] column[" + posInLine + "]");
        }
        tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine, Kind.EOF.text));
        return this;
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
