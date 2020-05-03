package cop5556sp17;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 *  @author Anurag Peshne
	 */
	public static void innerException(Exception e) throws SyntaxException, IllegalCharException, IllegalNumberException{
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
	}

	class Tester {
		private String input;
		private String method;

		private Tester(String input, String method) {
			this.input = input;
			this.method = method;
		}

		Tester(String input) {
			this(input, "parse");
		}

		private void test() throws IllegalCharException, IllegalNumberException, SyntaxException {
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
				innerException(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/*************************************************End of Tester **********************************************/
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("prog { ab <- 12; }", "parse").test();
		new Tester("prog boolean x { ab <- 12; }", "program").test();
		new Tester("prog boolean b, integer i { ab <- 12; }", "program").test();
		new Tester("prog {}", "program").test();
		thrown.expect(SyntaxException.class);
		new Tester("prog1 { an <- 12 }").test();
		new Tester("prog1 boolean { an <- 12 }").test();
	} // done

	@Test
	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester(" x <- 8", "assign").test();
		new Tester("x <- y + 8", "assign").test();
		new Tester("x <- 7", "assign").test();

		thrown.expect(SyntaxException.class);
		new Tester("<-", "arrowOp").test();
	}

	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester(" color -> bob", "chain").test();
		new Tester("blur |-> y -> z", "chain").test();
		new Tester("show -> y", "chain").test();

		thrown.expect(SyntaxException.class);
		new Tester("<-", "arrowOp").test();
	}

	@Test  // returns a Block
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("{}", "block").test();
		new Tester("{integer b}", "block").test(); // {integer boy }
		new Tester("{ sleep x + y ;}", "block").test(); // {integer boy }
		new Tester("{ boolean bool a <- true ;}", "block").test(); //
		new Tester("{ if (x) { y <- true ;} }", "block").test(); //


		thrown.expect(SyntaxException.class);
		new Tester("0url").test();
		new Tester("{ integer bob}").test();
		new Tester("{ sleep x + y }", "block").test(); // {integer boy }
	}

	/*******************************************   decs *************************************************/
	@Test  // returns one paramdec token
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("boolean x", "paramDec").test();
		new Tester("url y", "paramDec").test();
		new Tester("integer i", "paramDec").test();
//		new Tester("bowl", "paramDec").test();  //TODO expected to fail. need to add kinds array

		thrown.expect(SyntaxException.class);
		new Tester("0url").test();
		new Tester("bob").test();
	}

	@Test  // returns one paramdec token
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("boolean a", "dec").test();
		new Tester("image b", "dec").test();
//		new Tester(" bob", "dec").test(); //TODO Only returns first token. Should it fail if 2 tokens are passed?

		thrown.expect(SyntaxException.class);
		new Tester("0url").test();
		new Tester("bob").test();
	}

	/***************************************** Statements ***********************************************/

	@Test
	public void testStatementWhile() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " while ( x + 6) {}";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		parser.whileStatement();

		input = " while ( x * 6) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).whileStatement();

		thrown.expect(SyntaxException.class);
		input = " if ( x + 6) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).whileStatement();

		input = " if ( + ) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).whileStatement();
	}

	@Test
	public void testStatementIf() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " if ( x + 6 ) {}";
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		parser.ifStatement();

		input = " if ( k ) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).ifStatement();

		input = " if (3) {}";
		scanner = new Scanner(input).scan();
		new Parser(scanner).ifStatement();

		input = " if () {}"; // requires factor between ()
		thrown.expect(SyntaxException.class);
		scanner = new Scanner(input).scan();
		new Parser(scanner).ifStatement();

		input = " if ( {}"; //
		thrown.expect(SyntaxException.class);
		scanner = new Scanner(input).scan();
		new Parser(scanner).ifStatement();

		input = " if () }"; //
		thrown.expect(SyntaxException.class);
		scanner = new Scanner(input).scan();
		new Parser(scanner).ifStatement();
	}

	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
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
	public void testStatement()  throws IllegalCharException, IllegalNumberException, SyntaxException {
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

	/***************************************** Expressions **********************************************/

	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester(" color + col * co >= john", "expression").test();
		new Tester("x + y", "expression").test();
		new Tester("x <= y", "expression").test();

		thrown.expect(SyntaxException.class);
		new Tester("<-", "arrowOp").test();
	}

	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).factor();

		input = "( x + 3 ) * x + 3 )"; // TODO should this throw an error?
		scanner = new Scanner(input).scan();
		new Parser(scanner).factor();

		input = "abc";
		scanner = new Scanner(input).scan();
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
	public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "a + a - a / a * a | a & a % a ";
		new Parser(new Scanner(input).scan()).term();

		input = "0  + another - second * word";
		Scanner scanner = new Scanner(input).scan();
		new Parser(scanner).term();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();

		System.out.print(scanner); // TODO change to something useful
		Parser parser = new Parser(scanner);
		parser.arg();

		input = "  (3,5, bob, john ) ";
		scanner = new Scanner(input).scan();
		new Parser(scanner).arg();

		thrown.expect(SyntaxException.class);
		input = "  (3,) ";
		scanner = new Scanner(input).scan();
		new Parser(scanner).arg();

		input = "  (3, 6, ";
		scanner = new Scanner(input).scan();
		new Parser(scanner).arg();

		input = "  3,) ";
		scanner = new Scanner(input).scan();
		new Parser(scanner).arg();
	}

	/*************************************** Token OpChains *********************************************/

	@Test
	public void testImageOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("width", "imageOp").test();
		new Tester("height", "imageOp").test();
		new Tester("scale", "imageOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("/").test();
		new Tester("*").test();
	}

	@Test
	public void testStrongOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("%", "strongOp").test();
		new Tester("&", "strongOp").test();
		new Tester("/", "strongOp").test();
		new Tester("*", "strongOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("+").test();
		new Tester("-").test();
	}

	@Test
	public void testRelOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("<", "relOp").test();
		new Tester("<+", "relOp").test();
		new Tester(">", "relOp").test();
		new Tester(">=", "relOp").test();
		new Tester("==", "relOp").test();
		new Tester("!=", "relOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("+").test();
		new Tester("-").test();
	}

	@Test
	public void testWeakOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("+", "weakOp").test();
		new Tester("-", "weakOp").test();
		new Tester("|", "weakOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("/").test();
		new Tester("*").test();
	}

	@Test
	public void testFrameOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("yloc", "frameOp").test();
		new Tester("xloc", "frameOp").test();
		new Tester("show", "frameOp").test();
		new Tester("hide", "frameOp").test();
		new Tester("move", "frameOp").test();

	}

	@Test
	public void testFilterOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("blur", "filterOp").test();
		new Tester("convolve", "filterOp").test();
		new Tester("gray", "filterOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("->", "filterOp").test();
		new Tester("|->", "filterOp").test();
	}

	@Test
	public void testArrowOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		new Tester("->", "arrowOp").test();
		new Tester("|->", "arrowOp").test();

		thrown.expect(SyntaxException.class);
		new Tester("<-", "arrowOp").test();
	}

	/******************************** End of File  ******************************************************/
}