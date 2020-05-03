package cop5556sp17;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

public class ScannerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testEmpty() throws IllegalCharException, IllegalNumberException {
        String input = "";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void testSemiConcat0() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "\n\n \r;";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        String text = SEMI.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());

    }
    @Test
    public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";;;";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        assertEquals(0, token.pos);
        String text = SEMI.getText();
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

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    /**
     * This test illustrates how to check that the Scanner detects errors properly.
     * In this test, the input contains an int literal with a value that exceeds the range of an int.
     * The scanner should detect this and throw and IllegalNumberException.
     *
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    @Test
    public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
        String input = "99999999999999999";
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalNumberException.class);
        scanner.scan();
    }

    @Test
    public void testCommentError() throws IllegalCharException, IllegalNumberException{
        String input = "I love /* beard";
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
    }

    @Test
    public void test0() throws IllegalCharException, IllegalNumberException {
        String input = "0 0   0.  \r\n  123";
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
    }

    @Test
    public void test5() throws IllegalCharException, IllegalNumberException {
        String input = "|;|--->->-|->";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void test7() throws IllegalCharException, IllegalNumberException {
        String input = "123()+4+54321";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void test8() throws IllegalCharException, IllegalNumberException {
        String input = "a+b;a23a4";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void test11() throws IllegalCharException, IllegalNumberException {
        String input = "abc! !d";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }


    @Test
    public void test02() throws IllegalCharException, IllegalNumberException {
        String input = "0 0 0 \n\n 123";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void test04() throws IllegalCharException, IllegalNumberException {
        String input = "/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }


    @Test
    public void test9() throws IllegalCharException, IllegalNumberException {
        String input = "ifwhile;if;while;boolean;boolean0;integer;integer32|->frame->-image";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        assertEquals(IDENT, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(KW_IF, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(KW_WHILE, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(KW_BOOLEAN, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(IDENT, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(KW_INTEGER, scanner.nextToken().kind);
        assertEquals(SEMI, scanner.nextToken().kind);
        assertEquals(IDENT, scanner.nextToken().kind);
        Token token = scanner.nextToken();
        assertEquals(BARARROW, token.kind);
        Token token2 = scanner.nextToken();
        assertEquals(KW_FRAME, token2.kind);
        assertEquals(ARROW, scanner.nextToken().kind);
        assertEquals(MINUS, scanner.nextToken().kind);
        assertEquals(KW_IMAGE, scanner.nextToken().kind);

    } // Incorrect token position.  expected:<54> but was:<52>


    @Test
    public void test13() throws IllegalCharException, IllegalNumberException {
        String input = "\n\n \r;";
        Scanner scanner = new Scanner(input);
        scanner.scan();

    }//Incorrect token position.  expected:<4> but was:<0>

    @Test
    public void test14() throws IllegalCharException, IllegalNumberException {
        String input = "a\nbc! !\nd";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        assertEquals(IDENT, token.kind);

        Scanner.Token token2 = scanner.nextToken();
        assertEquals(IDENT, token2.kind);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(NOT, token3.kind);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(NOT, token4.kind);

        Scanner.Token token5 = scanner.nextToken();
        assertEquals(IDENT, token5.kind);
        assertEquals("d", token5.getText());

    } // Incorrect line #.  expected:<1> but was:<0>

    @Test
    public void test15() throws IllegalCharException, IllegalNumberException {
        String input = "/*...*/a/***/\nbc!/ /*/ /**/ !\nd/*.**/";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(14, token2.pos);

    } // Incorrect token position.  expected:<14> but was:<0>

    @Test
    public void test18() throws IllegalCharException, IllegalNumberException {
        String input = "/* * ** */\nabc";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        assertEquals(11, token.pos);

    } // Incorrect token position.  expected:<11> but was:<0> 'a'

    @Test
    public void test25() throws IllegalCharException, IllegalNumberException {
        String input = "show\r\n hide \n move \n file";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        assertEquals(4, token.length);
        assertEquals(0, token.pos);
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(4, token1.length); //hide
        assertEquals(7, token1.pos);
//	        Scanner.Token token2 = scanner.nextToken();

    } // Incorrect token position.  expected:<7> but was:<1> (hide?)

    @Test
    public void testWierdZero2() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "0 0   0  \r\n  123";
        Scanner scanner = new Scanner(input);         //create and initialize the scanner
        scanner.scan();

        Scanner.Token token = scanner.nextToken(); //
        Scanner.Token token1 = scanner.nextToken();
        Scanner.Token token2 = scanner.nextToken();

        assertEquals(INT_LIT, token2.kind);
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(INT_LIT, token3.kind);
        assertEquals(13, token3.pos);
        assertEquals(3, token3.length);
        assertEquals("123", token3.getText());
    } // Incorrect token position.  expected:<13> but was:<2> (0)

    @Test
    public void testWierdZeros() throws IllegalCharException, IllegalNumberException {
        String input = "000123";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        assertEquals(1, token.length);
        System.out.println(token.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(1, token.length);

    } // Incorrect token length.  expected:<1> but was:<6> (0)


    @Test
    public void testWierdZeros3() throws IllegalCharException, IllegalNumberException {
        String input = "0001230009";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();
        assertEquals(1, token.length);
        Scanner.Token token1 = scanner.nextToken();
        Scanner.Token token2 = scanner.nextToken();
        Scanner.Token token3 = scanner.nextToken();
    } // Incorrect token length.  expected:<1> but was:<6> (0)

    /** tester
     private void printScannerDeets(Scanner scanner) {
     while (scanner.peek() != null) {
     Scanner.Token t = scanner.nextToken();
     System.out.println(t.getLinePos());
     System.out.println(t.kind);
     System.out.println(t.getText());
     System.out.println(t.length);
     System.out.println("~~~~~~~~");
     }
     }
     */

}
