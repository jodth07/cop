/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void doTest(String input, Class<? extends Throwable> expectedException) throws Exception {
		Scanner scanner = new Scanner(input).scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		if (expectedException != null) {
			thrown.expect(expectedException);
		}
		program.visit(v, null);
	}

	private void doTest(String input) throws Exception {
		doTest(input, null);
	}

	@Test
	public void testAssignmentBoolLit0() throws Exception {
		doTest("p {\nboolean y \ny <- false;}");
	}

	@Test
	public void testAssignmentBoolLitError0() throws Exception {
		doTest("p {\nboolean y \ny <- 3;}", TypeCheckVisitor.TypeCheckException.class);
	}

	@Test
	public void testInteger() throws Exception{
		doTest("p {integer aNumber}");
	}

	@Test
	public void testBool() throws Exception{
		doTest("p{boolean aBool}");
	}

	@Test
	public void testLongBool0() throws Exception{
		doTest("p {integer aNumber\nboolean aBool\naNumber <- 10;\naBool <- false;}");
	}

	@Test
	public void testAssignmentIntLitErr0() throws Exception {
		TypeCheckVisitor v = new TypeCheckVisitor();

		String input = "p {\ninteger y \ny <- true;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssignmentIntLin0() throws Exception {

		String input = "p {\ninteger y \ny <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		TypeCheckVisitor v = new TypeCheckVisitor();

		ASTNode program = parser.parse();
			program.visit(v, null);
	}

	@Test
	public void testMissingVar() throws Exception {
		doTest("p {\ny <- 3;}", TypeCheckVisitor.TypeCheckException.class);
	}

	@Test
	public void testWhile() throws Exception {
		doTest("p {\nboolean y \nwhile (y) {}}");
	}

	@Test
	public void testWhileError0() throws Exception {
		doTest("p {\nwhile (y) {}}", TypeCheckVisitor.TypeCheckException.class);
	}

	@Test
	public void testWhileError1() throws Exception {
		doTest("p {\ninteger y \nwhile (y) {}}", TypeCheckException.class);
	}

	@Test
	public void testLongBool() throws Exception{
		doTest(" p{ integer aNumber\n" + " boolean aBool\n" + " aNumber <- 10;\n" + " aBool <- false;}");
	}

	/***************************************** Statements ***********************************************/

	/***************************************** Chains  **************************************************/

	/***************************************** Expressions **********************************************/

	@Test
	public void test0() throws Exception{
		doTest(" p{ }");
	}

	@Test
	public void test1() throws Exception{
		doTest(" p{ integer x }");
	}

	@Test
	public void test3() throws Exception{
		doTest("p {y <- 1; \ninteger x\n y <- 0; \ninteger y}"); //
	}

	@Test
	public void test2() throws Exception{
		doTest(" p{ integer x\n " +
				"boolean b\n b <- true;"+
				"boolean z\n z <- true;"+
				"x <- 3; " +
				"if (b) { x <- 6;}"+
				"}");
	}
}
