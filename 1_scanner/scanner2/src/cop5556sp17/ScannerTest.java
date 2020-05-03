package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

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
	public void testEOF() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);
	}

	/*
		KEYWORDS TEST
	 */
	@Test
	public void testKWInteger() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "integer";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_INTEGER, token.kind);
		assertEquals(0, token.pos);
		String text = KW_INTEGER.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}

	@Test
	public void testKWBoolean() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "boolean";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_BOOLEAN, token.kind);
		assertEquals(0, token.pos);
		String text = KW_BOOLEAN.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}

	@Test
	public void testKWImage() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "image";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_IMAGE, token.kind);
		assertEquals(0, token.pos);
		String text = KW_IMAGE.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}


	/*
	 KEYWORDS PLUS TEST
	 */
	@Test
	public void testKWFramePlus() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = " frame";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_FRAME, token.kind);
		assertEquals(1, token.pos);
		String text = KW_FRAME.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}

	@Test
	public void testKWUrlPlus() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "url ";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_URL, token.kind);
		assertEquals(0, token.pos);
		String text = KW_URL.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}

	@Test
	public void testKWFilePlus() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = " file ";

		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();

		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_FILE, token.kind);
		assertEquals(1, token.pos);
		String text = KW_FILE.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token2.kind);
	}

	@Test
	public void testSemi() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = "; ";

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

		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
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

//TODO  more tests
	
}
