/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 * 
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import cop5556sp17.AST.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.TypeCheckVisitor.TypeCheckException;
//import sun.jvm.hotspot.oops.Instance;

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

	/***************************************** Errors  **************************************************/

	@Test
	public void testFrameOp1() throws Exception{
		doTest("testFrame url u1, url u2, file file1 {frame fra1 frame fra2 image img \nfra1 -> move (screenheight, screenwidth) -> xloc;\n img -> fra2; img -> file1;}");
	} //Unexpected error happened in source code.


	@Test
	public void testFrameOp() throws Exception{
		doTest("testFrame url u1, url u2, file file1 {frame fra1 frame fra2 image img \nfra1 -> move (screenheight, screenwidth) -> xloc;\n }");
	} //Unexpected error happened in source code.

	@Test
	public void testTwoParamserror() throws Exception{
		doTest(" abc integer x, integer x {} ", TypeCheckException.class);
	} // Expected test to throw an instance of cop5556sp17.TypeCheckVisitor$TypeCheckException

	@Test
	public void testScope() throws Exception{
		doTest("p integer a, integer b {image img1 image img2 if(img1 != img2) {image a a <- img1; } if(a != b) {boolean a a <- img1 != img2; }}");
	} // Unexpected error happened in source code.

	@Test
	public void testBinaryExpr3Error() throws Exception{
		doTest("p {integer x integer y boolean z \nz <- x + y;\n}", TypeCheckException.class);
	} // Expected: an instance of cop5556sp17.TypeCheckVisitor$TypeCheckException but <java.lang.ClassCastException: cop5556sp17.AST.Dec cannot be cast to java.lang.Integer> is a java.lang.ClassCastException

	@Test
	public void testImageOp3() throws Exception{
		doTest("prog  boolean y , file x {\n integer z \n scale(100) -> width; blur -> y; convolve -> blur -> gray |-> gray -> width;}");
	} // Unexpected error happened in source code

	@Test
	public void complicatedProgram0() throws Exception {
		doTest("prog1  file file1, integer itx, boolean b1{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep itx+ii1; while (b1){if(bi1)\n{sleep ii1+itx*2;}}\nfile1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)|-> gray;\nii1 <- 12345+54321;}");
	}// Unexpected error happened in source code

	@Test
	public void testBinaryExpr101() throws Exception{
		doTest("p {integer x integer y integer z \nz <- x + y;\nz <- x-y;\nx <- z/y;\nz <- x / y;\nx <- z*z;\n}");

	} // Unexpected error happened in source code.

	@Test
	public void testBinaryExpr102() throws Exception{
		doTest("p {integer x integer y integer z \nz <- x + y;}");
	} // Unexpected error happened in source code.

	@Test
	public void testBinaryExpr103() throws Exception{
		doTest("p {integer x integer y integer z \nx <- z*z;\n}");
	} // Unexpected error happened in source code.

	@Test
	public void testBinaryExpr104() throws Exception{
		doTest("p {integer x integer y integer z \nz <- x / y;}");
	} // Unexpected error happened in source code.

	@Test
	public void testBinaryExpr4() throws Exception{
		doTest("p {integer x integer y boolean z \nz <- x < y; z <- x > y; z <- 33 <= 44; z <- 33 >= 55;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testBinaryExprBool() throws Exception{ //TODO NEED TO DOUBLE CHECK ASSIGNMENT AND LVALUE
		doTest("p {boolean x boolean y boolean z \nz <- x < y; z <- x > y; z <- 33 <= 44; z <- 33 >= 55;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testBinaryExpr11() throws Exception{
		doTest("p {integer x integer y boolean z \nz <- x < y;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testBinaryExpr12() throws Exception{
		doTest("p {integer x integer y boolean z \n z <- 33 >= 55;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testIntExpr00() throws Exception{
		doTest("p {integer x \nx <- 55 + 8;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testIntExpr01() throws Exception{
		doTest("p {integer x integer y  integer z \nx <- y + z;\n}");
	} //Unexpected error happened in source code

	@Test
	public void testBinaryExpr5() throws Exception{
		doTest("p {frame x frame y boolean z \nz <- x == y;\nz <- x != y;}");
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr8() throws Exception{
		doTest("p {frame x frame y boolean z \nz <- x >= y;\nz <- x <= y;}", TypeCheckException.class);
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr09() throws Exception{
		doTest("p {boolean x boolean y boolean z \nz <- x >= y;\nz <- x <= y;}");
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr08() throws Exception{
		doTest("p {boolean x boolean y boolean z \nz <- x > y;\nz <- x < y;}");
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr07() throws Exception{
		doTest("p {integer x integer y boolean z \nz <- x >= y;\nz <- x <= y;}");
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr06() throws Exception{
		doTest("p {integer x integer y boolean z \nz <- x > y;\nz <- x < y;}");
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr06error() throws Exception{
		doTest("p {integer x boolean y boolean z \nz <- x > y;\nz <- x < y;}", TypeCheckException.class);
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr06error0() throws Exception{
		doTest("p {frame x frame y boolean z \nz <- x > y;\nz <- x < y;}", TypeCheckException.class);
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr10() throws Exception{ // TODO should this work?
		doTest("p {image x image y boolean z \nz <- x >= y;\nz <- x <= y;}", TypeCheckException.class);
	} // Unexpected error happened in source code

	@Test
	public void testBinaryExpr9() throws Exception{
		doTest("p {frame x frame y boolean z \nz <- x == y;\nz <- x != y;}");
	} // Unexpected error happened in source code

	@Test
	public void test7() throws Exception{
		doTest(" p{image i frame f \n i -> f -> xloc;}");
	}

	@Test
	public void test6() throws Exception{
		doTest(" p{integer x \nx <- 4 + 6;}");
	}

	@Test
	public void testTimes() throws Exception{
		doTest(" p{integer x integer y integer z\nz <- x * y;}");
	}

	@Test
	public void testTimes02() throws Exception{
		doTest(" p{integer x \nx <- 4 * 6;}");
	}

	@Test
	public void testbool02() throws Exception{
		doTest(" p{boolean x \nx <- 4 == 6;}");
	}

	@Test
	public void testExpressboolserror() throws Exception{
		doTest(" p{integer x integer y integer z\nz <- x > y;}", TypeCheckException.class);
	}

	@Test
	public void testExpressbools() throws Exception{
		doTest(" p{integer x integer y boolean z\nz <- x > y;}");
	}

	/***************************************** Endors  **************************************************/

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
				"integer r "+
				"r <- 6; " +
				"if (b) { x <- 6;}"+
				"}");
	}

	@Test
	public void test4() throws Exception{ // TODO SERIOUSLY
		doTest(" p{ image y y |-> blur;}");
	}

	@Test
	public void test4error() throws Exception{ // TODO SERIOUSLY
		doTest(" p{ image y image x y |-> x;}", TypeCheckException.class);
	}

	@Test
	public void test5() throws Exception{
		doTest(" p{integer y image i y -> i; }", TypeCheckException.class);
	}
}
