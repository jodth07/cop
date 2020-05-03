package cop5556sp17;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static cop5556sp17.ParserTest.innerException;
import static cop5556sp17.Scanner.Kind.PLUS;
import static org.junit.Assert.assertEquals;

public class ASTTest {

	private static final boolean doPrint = true;
	private static void show(Object s){
		if(doPrint){System.out.println(s);}
	}

	/**
	 * Modified Tester
	 * Original @author Anurag Peshne
	 */
	private class Tester {
		private String input;
		private String method;

		private Tester(String input, String method) {
			this.input = input;
			this.method = method;
		}

		Tester(String input) {
			this(input, "parse");
		}

		private Object test() throws IllegalCharException, IllegalNumberException, SyntaxException {
			Scanner scanner = new Scanner(this.input);
			scanner.scan();
			Parser parser = new Parser(scanner);
			try {
				return Parser.class.getDeclaredMethod(this.method).invoke(parser);
			} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.reflect.InvocationTargetException e) {
				innerException(e);
			}
			return null;
		}
	}

	/************************************* End Parser Tests *********************************************/
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/****************************************** Parser Tests Failed? ************************************/

	//	Failed Test Cases:
//	testImageOpStatement3: Testing failed with input: "tos url u,\n integer x\n{integer y image i u -> i; i -> height -> x; frame f i -> scale (x) -> f;}". saw IDENT expected LBRACE
//	testProg2: Testing failed with input: "p url u1 {}". saw IDENT expected LBRACE
//	testImageOpStatement: Testing failed with input: "". saw IDENT expected LBRACE
//	testNestedRenameOk: Testing failed with input: "abc\n{integer x\nif(true){integer x}\n}". saw KW_IF expected [Lcop5556sp17.Scanner$Kind;@315cb0c2
//	testProg1e: Testing failed with input: "p url u1, url u2, file f1, file f2, integer i {}". saw IDENT expected LBRACE


	@Test
	public void testImageOpStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" tos url u,\n integer x\n{integer y image i u -> i; i -> height -> x; frame f i -> scale (x) -> f;} ", "parse").test()); //TODO, should this not return?
		for (Object result : results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE

	@Test
	public void testImageOpStatement() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester("tos integer x\n{image i frame f i -> scale (x) -> f;}", "parse").test()); //TODO, should this not return?
		for (Object result : results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE

	@Test
	public void testProg2() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("p url u1 {}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testProg3() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("tos integer x {}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testProg6() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("tos integer x {image i}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testProg7() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("tos integer x {image i frame f}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testProg8() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("tos integer x\n {frame i frame f i -> scale (x) -> f;}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed


	@Test
	public void testProg4() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("p boolean x {}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testProg5() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("p file x {}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // saw IDENT expected LBRACE --> passed

	@Test
	public void testNestedRenameOk() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester("prog { ab <- 12; }", "parse").test());
		results.add(new Tester("abc\n{integer x\nif(true){integer x}\n}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // passed // Not sure what is to be tested

	@Test
	public void testImageOpChain0() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" tos integer x\n{image i frame f i -> scale (x) -> f;} ", "parse").test()); //TODO, should this not return?
		for (Object result : results) {
			assertEquals(Program.class, result.getClass());
		}
	}  // failed ... different reasons

	@Test
	public void testProg1e() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO should this failed ? and why?
		List<Object> results = new ArrayList<>();
		results.add(new Tester("p url u1, url u2, file f1, file f2, integer i {}", "parse").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // done

	/****************************************** Parser Tests Modified ***********************************/
	@Test
	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("prog { ab <- 12; }", "parse").test());
		results.add(new Tester("prog boolean b { ab <- 12; }", "program").test());
		results.add(new Tester("prog boolean b, integer i { ab <- 12; }", "program").test());
		results.add(new Tester("prog {}", "program").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("prog1 { an <- 12 }").test());
		results.add(new Tester("prog1 boolean { an <- 12 }").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // done

	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester("prog { ab <- 12; }", "parse").test());
		results.add(new Tester("p url u1, url u2, file f1, file f2, integer i {}", "parse").test());
		results.add(new Tester("prog boolean b { ab <- 12; }", "program").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // done

	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException { // TODO failed
		List<Object> results = new ArrayList<>();
		results.add(new Tester("prog { ab <- 12; }", "parse").test());
		results.add(new Tester("prog boolean b { ab <- 12; }", "program").test());
		results.add(new Tester("p url u1, url u2, file f1, file f2, integer i {}", "program").test());
		for (Object result: results) {
			assertEquals(Program.class, result.getClass());
		}
	} // done

	@Test  // returns a Block
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("{}", "block").test());
		results.add(new Tester("{integer b}", "block").test()); // {integer boy }
		results.add(new Tester("{ sleep x + y ;}", "block").test()); // {integer boy }
		results.add(new Tester("{ boolean bool a <- true ;}", "block").test()); //
		results.add(new Tester("{ if (x) { y <- true ;} }", "block").test()); //
		for (Object result: results) {
			assertEquals(Block.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("0url").test());
		results.add(new Tester("{ integer bob}").test());
		results.add(new Tester("{ sleep x + y }", "block").test());
		for (Object result: results) {
			assertEquals(Block.class, result.getClass());
		}
	}

	/********************************************** Chains **********************************************/
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" color -> bob", "chain").test());
		results.add(new Tester("blur |-> y -> z", "chain").test());
		results.add(new Tester("show -> y", "chain").test());
		results.add(new Tester("a -> b -> scale (3 * 4) |-> my_ident", "chain").test());
		for (Object result: results) {
			assertEquals(BinaryChain.class, result.getClass());
		}
		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("<-", "arrowOp").test());
		for (Object result: results) {
			assertEquals(BinaryChain.class, result.getClass());
		}
	}

	/*************************************** Expressions ************************************************/
	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" color + col * co >= john", "expression").test());
		results.add(new Tester("x + y", "expression").test());
		results.add(new Tester("x <= y", "expression").test());
		for (Object result: results) {
			assertEquals(BinaryExpression.class, result.getClass());
		}
		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("<-", "expression").test());
		for (Object result: results) {
			assertEquals(BinaryExpression.class, result.getClass());
		}
	}

	@Test
	public void testIdentExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" tok ", "expression").test());
		results.add(new Tester("x", "expression").test());
		for (Object result: results) {
			assertEquals(IdentExpression.class, result.getClass());
		}
	}

	@Test
	public void testIntLitExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" 9000 ", "expression").test());
		results.add(new Tester("6", "expression").test());
		for (Object result: results) {
			assertEquals(IntLitExpression.class, result.getClass());
		}
	}

	@Test
	public void testBoolExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" true", "expression").test());
		results.add(new Tester("false", "expression").test());
		for (Object result: results) {
			assertEquals(BooleanLitExpression.class, result.getClass());
		}
	}

	@Test
	public void testConstExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("screenwidth", "expression").test());
		results.add(new Tester("screenheight", "expression").test());
		for (Object result: results) {
			assertEquals(ConstantExpression.class, result.getClass());
		}
	}

	/********************************************* Statements *******************************************/
	@Test
	public void testAssignmentStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" x <- 8", "assign").test());
		results.add(new Tester("x <- y + 8", "assign").test());
		results.add(new Tester("x <- 7", "assign").test());
		for (Object result: results) {
			assertEquals(AssignmentStatement.class, result.getClass());
		}
		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("->", "assign").test());
		for (Object result: results) {
			assertEquals(AssignmentStatement.class, result.getClass());
		}
	}

	@Test
	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" if (y) {}", "statement").test()); //TODO should we be able to do if (x -y) {}?
		results.add(new Tester(" if (x) {x <- y + 8;}", "statement").test());
		results.add(new Tester("if (x) {}", "statement").test());
		for (Object result : results) {
			assertEquals(IfStatement.class, result.getClass());
		}
	}

	@Test
	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" while (y) {}", "statement").test()); //TODO should we be able to do if (x -y) {}?
		results.add(new Tester(" while (x) {x <- y + 8;}", "statement").test());
		results.add(new Tester("while (x) {}", "statement").test());
		for (Object result : results) {
			assertEquals(WhileStatement.class, result.getClass());
		}
	}

	@Test
	public void testSleepStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester(" sleep expre ", "sleepStatement").test()); //TODO, should this not return?
//		results.add(new Tester(" sleep x ", "statement").test());
//		results.add(new Tester(" sleep bob", "statement").test());
		for (Object result : results) {
			assertEquals(SleepStatement.class, result.getClass());
		}
	}

	/*******************************************   decs *************************************************/
	@Test  
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("boolean x", "paramDec").test());
		results.add(new Tester("url y", "paramDec").test());
		results.add(new Tester("integer i", "paramDec").test());
//		new Tester("bowl", "paramDec").test());
		for (Object result: results) {
			assertEquals(ParamDec.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("0url").test());
		results.add(new Tester("bob").test());
		for (Object result: results) {
			assertEquals(ParamDec.class, result.getClass());
		}
	}

	@Test  // 
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("boolean a", "dec").test());
		results.add(new Tester("image b", "dec").test());
//		new Tester(" bob", "dec").test());
		for (Object result: results) {
			assertEquals(Dec.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("0url").test());
		results.add(new Tester("bob").test());
		for (Object result: results) {
			assertEquals(Dec.class, result.getClass());
		}
	}

	/***************************** Tests Ops, return Scanner.Token class ********************************/
	@Test
	public void testWeakOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("+", "weakOp").test());
		results.add(new Tester("-", "weakOp").test());
		results.add(new Tester("|", "weakOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("/").test());
		results.add(new Tester("*").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testFrameOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("yloc", "frameOp").test());
		results.add(new Tester("xloc", "frameOp").test());
		results.add(new Tester("show", "frameOp").test());
		results.add(new Tester("hide", "frameOp").test());
		results.add(new Tester("move", "frameOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testFilterOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("blur", "filterOp").test());
		results.add(new Tester("convolve", "filterOp").test());
		results.add(new Tester("gray", "filterOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("->", "filterOp").test());
		results.add(new Tester("|->", "filterOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testArrowOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("->", "arrowOp").test());
		results.add(new Tester("|->", "arrowOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("<-", "arrowOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testImageOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("width", "imageOp").test());
		results.add(new Tester("height", "imageOp").test());
		results.add(new Tester("scale", "imageOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("/").test());
		results.add(new Tester("*").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testStrongOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("%", "strongOp").test());
		results.add(new Tester("&", "strongOp").test());
		results.add(new Tester("/", "strongOp").test());
		results.add(new Tester("*", "strongOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("+").test());
		results.add(new Tester("-").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testRelOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		List<Object> results = new ArrayList<>();
		results.add(new Tester("<", "relOp").test());
		results.add(new Tester("<+", "relOp").test());
		results.add(new Tester(">", "relOp").test());
		results.add(new Tester(">=", "relOp").test());
		results.add(new Tester("==", "relOp").test());
		results.add(new Tester("!=", "relOp").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}

		thrown.expect(Parser.SyntaxException.class);
		results.add(new Tester("+").test());
		results.add(new Tester("-").test());
		for (Object result: results) {
			assertEquals(Scanner.Token.class, result.getClass());
		}
	}

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());

		input = "980";
		parser = new Parser(new Scanner(input).scan());
		ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}

	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}

	/******************************** End of File  ******************************************************/
}
