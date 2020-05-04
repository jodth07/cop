package cop5556sp17;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {
    String input;
    String text;
    Scanner scanner;
    Scanner.Token token;

    @BeforeEach
    public void setUp(){
        input = "";
        text = "";
    }

    @Test
    public void testEmpty() throws IllegalCharException, IllegalNumberException {
        scanner = new Scanner(input);
        scanner.scan();
    }

    @AfterEach
    public void testFinalEOF() throws IllegalCharException, IllegalNumberException {
        //check that the scanner has inserted an EOF token at the end
        token = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF, token.kind);
        assertEquals(token.text, "eof");
    }

    @Test
    public void testInComment() throws IllegalCharException, IllegalNumberException {
        input = "*/";
        scanner = new Scanner(input);
        scanner.inComment = true;
        scanner.scan();

        //check that the scanner has inserted an EOF token at the end
        token = scanner.nextToken();
        assertEquals(Scanner.Kind.UNCOMMENT, token.kind);
        assertEquals(token.text, "*/");
        assertEquals(0, token.posInLine);
        assertFalse(scanner.inComment);
    }

    @Test
    public void testInOutComments() throws IllegalCharException, IllegalNumberException {
        input = "/*   */";
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        assertEquals(Scanner.Kind.COMMENT, token.kind);
        assertEquals(token.text, "/*");
        assertEquals(0, token.posInLine);

        //check that the scanner has inserted an EOF token at the end
        token = scanner.nextToken();
        assertEquals(Scanner.Kind.UNCOMMENT, token.kind);
        assertEquals(token.text, "*/");
        assertEquals(5, token.posInLine);
        assertFalse(scanner.inComment);
    }

    @Test
    public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
        //input string
        input = ";;;";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        assertEquals(0, token.pos);
        text = SEMI.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(SEMI, token1.kind);
        assertEquals(1, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(SEMI, token2.kind);
        assertEquals(2, token2.pos);
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
    }

    //    separator ::= 	;  | ,  |  (  |  )  | { | }
    @Test
    public void testSeparators() throws IllegalCharException, IllegalNumberException {
        input = "; , ( ) { }";
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        assertEquals(0, token.pos);
        text = SEMI.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(COMMA, token1.kind);
        text = COMMA.getText();
        assertEquals(2, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());

        Scanner.Token token2 = scanner.nextToken();
        assertEquals(LPAREN, token2.kind);
        assertEquals(4, token2.pos);
        text = LPAREN.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(RPAREN, token3.kind);
        assertEquals(6, token3.pos);
        text = RPAREN.getText();
        assertEquals(text.length(), token3.length);
        assertEquals(text, token3.getText());

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(LBRACE, token4.kind);
        assertEquals(8, token4.pos);
        text = LBRACE.getText();
        assertEquals(text.length(), token4.length);
        assertEquals(text, token4.getText());

        Scanner.Token token5 = scanner.nextToken();
        assertEquals(RBRACE, token5.kind);
        assertEquals(10, token5.pos);
        text = RBRACE.getText();
        assertEquals(text.length(), token5.length);
        assertEquals(text, token5.getText());

//        Scanner.Token token6 = scanner.nextToken();
//        assertEquals(OR, token6.kind);
//        assertEquals(12, token6.pos);
//        text = OR.getText();
//        assertEquals(text.length(), token6.length);
//        assertEquals(text, token6.getText());
//
//        Scanner.Token token7 = scanner.nextToken();
//        assertEquals(AND, token7.kind);
//        assertEquals(14, token7.pos);
//        text = AND.getText();
//        assertEquals(text.length(), token7.length);
//        assertEquals(text, token7.getText());

    }

    @Test
    public void testModPlusTimeDivConcat() throws IllegalCharException, IllegalNumberException {
        input = " % +  * / ";
        scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token8 = scanner.nextToken();
        assertEquals(MOD, token8.kind);
        assertEquals(1, token8.pos);
        text = MOD.getText();
        assertEquals(text.length(), token8.length);
        assertEquals(text, token8.getText());

        Scanner.Token token9 = scanner.nextToken();
        assertEquals(PLUS, token9.kind);
        assertEquals(3, token9.pos);
        text = PLUS.getText();
        assertEquals(text.length(), token9.length);
        assertEquals(text, token9.getText());

        Scanner.Token token10 = scanner.nextToken();
        assertEquals(TIMES, token10.kind);
        assertEquals(6, token10.pos);
        text = TIMES.getText();
        assertEquals(text.length(), token10.length);
        assertEquals(text, token10.getText());

        token = scanner.nextToken();
        assertEquals(DIV, token.kind);
        assertEquals(8, token.pos);
        text = DIV.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
    }

    @Test
    public void testComaConcat() throws IllegalCharException, IllegalNumberException {
        //input string
        input = ",,,";
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(COMMA, token.kind);
        assertEquals(0, token.pos);

        text = COMMA.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(COMMA, token1.kind);
        assertEquals(1, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(COMMA, token2.kind);
        assertEquals(2, token2.pos);
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
    }

    @Test
    public void testReturnLine() throws IllegalCharException, IllegalNumberException {
        //input string
        input = "\n(\r {";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(LPAREN, token.kind);
        assertEquals(1, token.pos);
        assertEquals(1, token.line);
//        assertEquals(0, token.posInLine);
        text = LPAREN.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(LBRACE, token.kind);
        assertEquals(4, token.pos);
        assertEquals(2, token.line);
        assertEquals(1, token.posInLine);
        text = LBRACE.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

    }

    @Test
    public void testKeyWordIfWhile() throws IllegalCharException, IllegalNumberException {
        input = "if while";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_IF.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_IF, token.kind);
        assertEquals(0, token.posInLine);

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_WHILE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_WHILE, token.kind);
        assertEquals(3, token.posInLine);
    }

    //    boolean_literal ::= true | false
    @Test
    public void testBooleanLiterals() throws IllegalCharException, IllegalNumberException {
        input = "true false boolean";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_TRUE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_TRUE, token.kind);
        assertEquals(0, token.posInLine);

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_FALSE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_FALSE, token.kind);
        assertEquals(5, token.posInLine);

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_BOOLEAN.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_BOOLEAN, token.kind);
        assertEquals(11, token.posInLine);

    }

    //frame_op_keyword ∷= xloc | yloc | hide | show | move
    @Test
    public void testFrameOPKeywords() throws IllegalCharException, IllegalNumberException {
        input = "xloc | yloc | hide | show | move";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_XLOC.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_XLOC, token.kind);
        assertEquals(0, token.posInLine);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_YLOC.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_YLOC, token.kind);
        assertEquals(7, token.posInLine);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_HIDE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_HIDE, token.kind);
        assertEquals(14, token.posInLine);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_SHOW.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_SHOW, token.kind);
        assertEquals(21, token.posInLine);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_MOVE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_MOVE, token.kind);
        assertEquals(28, token.posInLine);

    }

    //image_op_keyword ∷= width | height
    @Test
    public void testImageOPKeywords() throws IllegalCharException, IllegalNumberException {
        input = "width | \nheight";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = OP_WIDTH.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_WIDTH, token.kind);
        assertEquals(0, token.posInLine);

        //get the first token and check its kind, position, and contents
        scanner.nextToken();
        token = scanner.nextToken();
        text = OP_HEIGHT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_HEIGHT, token.kind);
        assertEquals(0, token.posInLine);

    }

    //filter_op_keyword ∷= gray | convolve | blur | scale
    @Test
    public void testFilterOPKeywords() throws IllegalCharException, IllegalNumberException {
        input = "gray | \nconvolve | blur | scale";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        text = OP_GRAY.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_GRAY, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(0, token.line);

        scanner.nextToken();
        token = scanner.nextToken();
        text = OP_CONVOLVE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_CONVOLVE, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(1, token.line);
        assertEquals(8, token.pos); // Unsure if return characters count

        scanner.nextToken();
        token = scanner.nextToken();
        text = OP_BLUR.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_BLUR, token.kind);
        assertEquals(11, token.posInLine);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_SCALE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_SCALE, token.kind);
        assertEquals(18, token.posInLine);
    }

    //	keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth
    @Test
    public void testRegularKeywords() throws IllegalCharException, IllegalNumberException {
        input = "integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        text = KW_INTEGER.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_INTEGER, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(0, token.line);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_BOOLEAN.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_BOOLEAN, token.kind);
        assertEquals(10, token.posInLine);
        assertEquals(0, token.line);
        assertEquals(10, token.pos); // Unsure if return characters count

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_IMAGE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_IMAGE, token.kind);
        assertEquals(20, token.posInLine);

        // url | file | frame | while | if | sleep | screenheight | screenwidth
        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_URL.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_URL, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_FILE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_FILE, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_FRAME.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_FRAME, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_WHILE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_WHILE, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_IF.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_IF, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = OP_SLEEP.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(OP_SLEEP, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_SCREENHEIGHT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_SCREENHEIGHT, token.kind);

        scanner.nextToken();
        token = scanner.nextToken();
        text = KW_SCREENWIDTH.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_SCREENWIDTH, token.kind);
    }

    //  operator ::=   	|  | &  |  ==  | !=  | < |  > | <= | >= | +  |  -  |  *   |  /   |  % | !  | -> |  |-> | <-
    @Test
    public void testOperatorsPart1() throws IllegalCharException, IllegalNumberException {
        input = "! != ";

        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        text = NOT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(NOT, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(0, token.line);

        token = scanner.nextToken();
        text = NOTEQUAL.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(NOTEQUAL, token.kind);
        assertEquals(2, token.posInLine);

    }


    //  operator ::=  < |  > | <= | >= | +
    @Test
    public void testOperatorsPart2() throws IllegalCharException, IllegalNumberException {
        input = "< > <= >= + ";
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        text = LT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(LT, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(0, token.line);

        token = scanner.nextToken();
        text = GT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(GT, token.kind);
        assertEquals(2, token.posInLine);

        token = scanner.nextToken();
        text = LE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(LE, token.kind);
        assertEquals(4, token.posInLine);

        token = scanner.nextToken();
        text = GE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(GE, token.kind);
        assertEquals(7, token.posInLine);

        token = scanner.nextToken();
        text = PLUS.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(PLUS, token.kind);
        assertEquals(10, token.posInLine);
    }

    //  operator ::=   -  |  *   |  /   |  % | !  | -> |  |-> | <-
    @Test
    public void testOperatorsPart3() throws IllegalCharException, IllegalNumberException {
        input = "- * /  % \n! -> |-> <-";
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        text = MINUS.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(MINUS, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(0, token.line);

        token = scanner.nextToken();
        text = TIMES.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(TIMES, token.kind);
        assertEquals(2, token.posInLine);

        token = scanner.nextToken();
        text = DIV.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(DIV, token.kind);
        assertEquals(4, token.posInLine);

        token = scanner.nextToken();
        text = MOD.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(MOD, token.kind);
        assertEquals(7, token.posInLine);

        token = scanner.nextToken();
        text = NOT.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(NOT, token.kind);
        assertEquals(0, token.posInLine);
        assertEquals(1, token.line);

        token = scanner.nextToken();
        text = ARROW.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(ARROW, token.kind);
        assertEquals(2, token.posInLine);

        token = scanner.nextToken();
        text = BARARROW.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(BARARROW, token.kind);
        assertEquals(5, token.posInLine);

        token = scanner.nextToken();
        text = ASSIGN.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(ASSIGN, token.kind);
        assertEquals(9, token.posInLine);
    }

    //	@Test
//	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
//		input = "99999999999999999";
//		scanner = new Scanner(input);
//		thrown.expect(IllegalNumberException.class);
//		scanner.scan();
//	}

}