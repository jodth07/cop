package cop5556sp17;

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

    @Test
    public void testEOF() throws IllegalCharException, IllegalNumberException {
        scanner = new Scanner(input);
        scanner.scan();

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF, token3.kind);
        assertEquals(token3.text, "eof");
    }

    @Test
    public void testInCommentException() throws IllegalCharException, IllegalNumberException  {
        input = "/*";
        scanner = new Scanner(input);
        assertThrows(IllegalCharException.class, scanner::scan);
    }

    @Test
    public void testNotCommentException() throws IllegalCharException, IllegalNumberException {
        input = "/*/";
        scanner = new Scanner(input);
        assertThrows(IllegalCharException.class, scanner::scan);
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
        assertEquals(0, token.pos);
        assertFalse(scanner.inComment);


        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
    }

    @Test
    public void testInOutComments() throws IllegalCharException, IllegalNumberException {
        input = "/*   */";
        scanner = new Scanner(input);
        scanner.scan();

        token = scanner.nextToken();
        assertEquals(Scanner.Kind.COMMENT, token.kind);
        assertEquals(token.text, "/*");

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(Scanner.Kind.UNCOMMENT, token2.kind);
        assertEquals(token2.text, "*/");
        assertEquals(5, token2.pos);
        assertFalse(scanner.inComment);

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
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
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
    }

    @Test
    public void testSemiCommaParensBracesOrAndConcat() throws IllegalCharException, IllegalNumberException {
        input = "; , ( ) { } | & ";
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

        Scanner.Token token6 = scanner.nextToken();
        assertEquals(OR, token6.kind);
        assertEquals(12, token6.pos);
        text = OR.getText();
        assertEquals(text.length(), token6.length);
        assertEquals(text, token6.getText());

        Scanner.Token token7 = scanner.nextToken();
        assertEquals(AND, token7.kind);
        assertEquals(14, token7.pos);
        text = AND.getText();
        assertEquals(text.length(), token7.length);
        assertEquals(text, token7.getText());

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token8 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token8.kind);
    }

    @Test
    public void testModPlusConcat() throws IllegalCharException, IllegalNumberException {
        input = " % + ";
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

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
    }

    @Test
    public void testTimeDivConcat() throws IllegalCharException, IllegalNumberException {
        input = " * / ";
        scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token10 = scanner.nextToken();
        assertEquals(TIMES, token10.kind);
        assertEquals(1, token10.pos);
        text = TIMES.getText();
        assertEquals(text.length(), token10.length);
        assertEquals(text, token10.getText());

        Scanner.Token token8 = scanner.nextToken();
        assertEquals(DIV, token8.kind);
        assertEquals(3, token8.pos);
        text = DIV.getText();
        assertEquals(text.length(), token8.length);
        assertEquals(text, token8.getText());

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
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
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
    }

    @Test
    public void testLeftParen() throws IllegalCharException, IllegalNumberException {
        //input string
        input = "(";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        assertEquals(LPAREN, token.kind);
        assertEquals(0, token.pos);
        text = LPAREN.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

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
        assertEquals(0, token.posInLine);
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


//	keyword ::= integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth
//filter_op_keyword ∷= gray | convolve | blur | scale
//image_op_keyword ∷= width | height
//frame_op_keyword ∷= xloc | yloc | hide | show | move
//boolean_literal ::= true | false

    @Test
    public void testKeyWordIfSpace() throws IllegalCharException, IllegalNumberException {
        input = "if ";

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
    }

    @Test
    public void testKeyWordIf() throws IllegalCharException, IllegalNumberException {
        input = "if";

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
    }

  @Test
    public void testKeyWordWhile() throws IllegalCharException, IllegalNumberException {
        input = "while";

        //create and initialize the scanner
        scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        token = scanner.nextToken();
        text = KW_WHILE.getText();
        assertEquals(text, token.getText());
        assertEquals(text.length(), token.length);
        assertEquals(KW_WHILE, token.kind);
        assertEquals(0, token.posInLine);
    }




//	@Test
//	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
//		input = "99999999999999999";
//		scanner = new Scanner(input);
//		thrown.expect(IllegalNumberException.class);
//		scanner.scan();
//	}


}
