package cop5556sp17;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Token;

import static cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

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
	
    @Test
    public void testKindsOfToken() throws IllegalCharException, IllegalNumberException {
        String[] idents = {"$ident", "_ident", "testIdent", "Testident", "testIdent00"};
        for (String ident : idents) {
            Token expected = new Token(IDENT, ident, 0, 0, ident.length());
            Scanner scanner = new Scanner(ident).scan();
            compareTokens(expected, scanner.nextToken());
        }
    }

    @Test
    public void testBadKindsOfToken() throws IllegalCharException, IllegalNumberException {
        String[] idents = {"0ident", "test◙"};
        for (String ident : idents) {
            try {
                new Scanner(ident).scan();
                errorCollector.addError(new Error("Was mistakenly able to process: "+ident));
            } catch (IllegalCharException ignored) {
            }
        }
    }

    @Test
    public void testSpaces() throws IllegalCharException, IllegalNumberException {
        Scanner scanner = new Scanner("test 012345").scan();
        Token expected0 = new Token(IDENT, "test", 0, 0, 4);
        Token expected1 = new Token(INT_LIT, "012345", 0, 5, 6);
        compareTokens(expected0, scanner.nextToken());
        compareTokens(expected1, scanner.nextToken());
    }

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    private void compareTokens(Token one, Token two) {
        errorCollector.checkThat(one.kind, IsEqual.equalTo(two.kind));
        errorCollector.checkThat(one.getLinePos().line, IsEqual.equalTo(two.getLinePos().line));
        errorCollector.checkThat(one.getLinePos().posInLine, IsEqual.equalTo(two.getLinePos().posInLine));
        errorCollector.checkThat(one.getLength(), IsEqual.equalTo(two.getLength()));
        if (one.kind == INT_LIT) errorCollector.checkThat(one.intVal(), IsEqual.equalTo(two.intVal()));
        errorCollector.checkThat(one.getText(), IsEqual.equalTo(two.getText()));
    }

    @Test
    public void testKeyWords() throws IllegalCharException, IllegalNumberException {
        for (Kind kind : Kind.values()) {
            if (kind != IDENT && kind != INT_LIT) {
                compareTokens(new Token(kind, 0, 0, kind.getText().length()),
                        new Scanner(kind.getText()).scan().nextToken());
            }
        }
    }

    @Test
    public void testKeyWordsWithSpaces() throws IllegalCharException, IllegalNumberException {
        String keyWordsWithSpaces = "integer integerUnoMan move * % show scale == \n\n< + };false;falsetastic<<-<<=";
        Scanner scanner = new Scanner(keyWordsWithSpaces).scan();
        List<Token> expectedTokens = new ArrayList<>();
        expectedTokens.add(new Token(KW_INTEGER, "integer", 0, 0, "integer".length()));
        expectedTokens.add(new Token(IDENT, "integerUnoMan", 0, 8, "integerUnoMan".length()));
        expectedTokens.add(new Token(KW_MOVE, "move", 0, 22, "move".length()));
        expectedTokens.add(new Token(TIMES, "*", 0, 27, "*".length()));
        expectedTokens.add(new Token(MOD, "%", 0, 29, "%".length()));
        expectedTokens.add(new Token(KW_SHOW, "show", 0, 31, "show".length()));
        expectedTokens.add(new Token(KW_SCALE, "scale", 0, 36, "scale".length()));
        expectedTokens.add(new Token(EQUAL, "==", 0, 42, "==".length()));
        expectedTokens.add(new Token(LT, "<", 2, 0, "<".length()));
        expectedTokens.add(new Token(PLUS, "+", 2, 2, "+".length()));
        expectedTokens.add(new Token(RBRACE, "}", 2, 4, "}".length()));
        expectedTokens.add(new Token(SEMI, ";", 2, 5, ";".length()));
        expectedTokens.add(new Token(KW_FALSE, "false", 2, 6, "false".length()));
        expectedTokens.add(new Token(SEMI, ";", 2, 11, ";".length()));
        expectedTokens.add(new Token(IDENT, "falsetastic", 2, 12, "falsetastic".length()));
        expectedTokens.add(new Token(LT, "<", 2, 23, "<".length()));
        expectedTokens.add(new Token(ASSIGN, "<-", 2, 24, "<-".length()));
        expectedTokens.add(new Token(LT, "<", 2, 26, "<".length()));
        expectedTokens.add(new Token(LE, "<=", 2, 27, "<=".length()));

        for (Token token : expectedTokens) {
            compareTokens(token, scanner.nextToken());
        }
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
