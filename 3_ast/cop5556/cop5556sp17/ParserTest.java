package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 *  @author Anurag Peshne
	 */
	private class Tester {
		private String input;
		private String method;

		public Tester(String input, String method) {
			this.input = input;
			this.method = method;
		}

		public Tester(String input) {
			this(input, "parse");
		}

		public void test() throws IllegalCharException, IllegalNumberException, SyntaxException {
			Scanner scanner = new Scanner(this.input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			try {
				Parser.class.getDeclaredMethod(this.method).invoke(parser);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (java.lang.reflect.InvocationTargetException e) {
				Exception innerException = (Exception) e.getCause();
				if (innerException.getClass() == SyntaxException.class) {
					throw (SyntaxException) innerException;
				} else if (innerException.getClass() == IllegalCharException.class) {
					throw (IllegalCharException) innerException;
				} else if (innerException.getClass() == IllegalNumberException.class) {
					throw (IllegalNumberException) innerException;
				} else {
					innerException.printStackTrace();
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}


	/*************************************************End of Tester **********************************************/

	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("prog1 { ab <- 12; }", "parse").test();
		new Tester("prog boolean { ab <- 12; }", "program").test();
		new Tester("prog boolean, integer { ab <- 12; }", "program").test();
		new Tester("prog {}", "program").test();

		thrown.expect(Parser.SyntaxException.class);
		new Tester("prog1 { an <- 12 }").test();
		new Tester("prog1 boolean { an <- 12 }").test();
	} // done



	@Test  // returns one paramdec token
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("boolean", "paramDec").test();
		new Tester("url", "paramDec").test();

		thrown.expect(Parser.SyntaxException.class);
		new Tester("0url").test();
		new Tester("bob").test();
	}


	@Test  // returns one paramdec token
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("boolean", "dec").test();
		new Tester("image", "dec").test();
//		new Tester(" bob", "dec").test(); // Only returns first token. Should it fail if 2 tokens are passed?

		thrown.expect(Parser.SyntaxException.class);
		new Tester("0url").test();
		new Tester("bob").test();
	}

	@Test  // returns a Block
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("{}", "block").test();
		new Tester("{integer bob}", "block").test();
		new Tester("{ boolean bool <- true ;}", "block").test(); //


		thrown.expect(Parser.SyntaxException.class);
		new Tester("0url").test();
		new Tester("bob").test();
	}

	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).factor();
	}

	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "ef *  7 / 9 % 6";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).elem();

		input = " 5 + ) / 9 + 6";  // TODO should this not throw an error?
		scanner = new Scanner(input).scan();
		new Parser(scanner).elem();

		input = "identifier * another / second & word % mod";
		scanner = new Scanner(input).scan();
		new Parser(scanner).elem();

		input = "identifier / another * second & word % mod";
		scanner = new Scanner(input).scan();
		new Parser(scanner).elem();
	}


	@Test
	public void testTerm0() throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = "a + a - a / a * a | a & a % a ";
		new Parser(new Scanner(input).scan()).term();

		input = "0  + another - second * word";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).term();
	}

	/**
	 * Parser 	* arg	* arrowOp	* assign
	 * block	* chain	* chainElem	* consume	* dec
	 * expression	* factor	* filterOp	* imageOp	* match	* paramDec
	 * parse	* program * statement
	 */

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();

		System.out.println(scanner); // TODO change to something useful
		Parser parser = new Parser(scanner);
		parser.arg();

		input = "  (3,) ";
		scanner = new Scanner(input).scan();
		parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testStatementwhile() throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = " while ( x + 6) {}";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		parser.statement();

		input = " while ( x + 6) {}";
		scanner = new Scanner(input).scan();
		parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testStatementIf() throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = " while ( x + 6 ) {}";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		parser.statement();

		input = " while ( x + 6 ) {}";
		new Parser(new Scanner(input).scan()).statement();
	}

	@Test
	public void testStatementChain() throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = " sleep ident + 6;";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		parser.statement();
	}

	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = " ident";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).chainElem();

		input = " blur ";
		scanner = new Scanner(input).scan();
		new Parser(scanner).chainElem();

		input = " blur (5)";
		scanner = new Scanner(input).scan();
		new Parser(scanner).chainElem();

		input = " hide (5, true)";
		scanner = new Scanner(input).scan();
		new Parser(scanner).chainElem();

		input = " scale (5, green)";
		scanner = new Scanner(input).scan();
		new Parser(scanner).chainElem();
	}

	@Test
	public void testStatement()  throws IllegalCharException, IllegalNumberException, Parser.SyntaxException {
		String input = "sleep a;";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).statement();

		input = "while (a) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).statement();

		input = "if (a) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).statement();

		input = "a -> a -> a -> a;";
		scanner = new Scanner(input).scan();
		new Parser(scanner).statement();

		input = "a <- a;";
		scanner = new Scanner(input).scan();
		new Parser(scanner).statement();

		input = " sleep ident + 6;";
		new Parser(new Scanner(input).scan()).statement();
	}
}
