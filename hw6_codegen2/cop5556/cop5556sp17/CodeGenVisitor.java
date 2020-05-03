//TODO INSTANCE OF PARAMDEC IS PROBABLY NOT WORKING (AKA ALWAYS FALSE)

package cop5556sp17;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;


import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.TypeCheckVisitor.*;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.AST.Type.*;

import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	int index = 0;
	int slot = 1;

//	String fieldName;
//	String fieldDesc;

	MethodVisitor mv; // visitor of method currently under construction
	FieldVisitor fv; //added

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	static class LocalAttribute{
		String ident;
		Label startLabel;
		Label endLabel;
		int slotNumber;
		Dec dec;
	}

	ArrayList<LocalAttribute> localVars = new ArrayList<>();
	ArrayList<Label> labels = new ArrayList<>();
	ArrayList<Dec> decs = new ArrayList<>();
	ArrayList<ParamDec> paramDecs = new ArrayList<>();

	/***********************************  Program, Block, Params and Decs *******************************/

	@Override // Good
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//		cw = new ClassWriter(0);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		/************** ************** constructor method ************** **************/

		System.out.println(" ---  constructor method---- "); //TODO cover
		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();

		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);

		// this is for convenience during development--you can see that the code is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");

		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		/**** Visiting ParamDec ***/
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params) {
			dec.visit(this, mv);
		}
		mv.visitInsn(RETURN);

		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);

		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);

		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);

		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		/************** ************** constructor end ************** **************/

		/************** ************** "main" method ************** **************/

		System.out.println(" ---  main method ---- ");


		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");

		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);

		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		/************** ************** "main" method end ************** **************/

		System.out.println(" ---  run method ---- ");


		// create run method //
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);

		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");

		program.getB().visit(this, null);

		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);

		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);

		//TODO  visit the local variables?
		for(Dec dec: decs){
			mv.visitLocalVariable(dec.getIdent().getText(), Type.getTypeName(dec.getFirstToken()).getJVMTypeDesc(), null, dec.getStartLabel(), dec.getEndLabel(), dec.getSlot());
		}

		mv.visitMaxs(10, 10);
		mv.visitEnd(); // end of run method

		cw.visitEnd();//end of class

		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override // Good
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {

		System.out.println(" ---  visitParamdec ---- ");

		String fieldName = paramDec.getIdent().getText();
		String fieldDesc = paramDec.getTypeName().getJVMTypeDesc(); // field Desc == fieldType
		String fieldSig;

		fv = cw.visitField(0, fieldName, fieldDesc, null, null);
		fv.visitEnd();

		mv.visitVarInsn(ALOAD, 0);

		switch (paramDec.getTypeName()){

			case INTEGER: // arg 0
//				mv.visitTypeInsn(NEW, "java/lang/Integer");
//				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(index++); // increases index in array
				mv.visitInsn(AALOAD);
				//			Integer.parseInt(arg);
//				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Integer", "<init>", "(Ljava/lang/String;)V", false);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
				// storing value
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
				mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);
				break;

			case BOOLEAN: // arg 1

//				mv.visitTypeInsn(NEW, "java/lang/Boolean");
//				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(index++); // increases index in array
				mv.visitInsn(AALOAD);
//				mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Boolean", "<init>", "(Ljava/lang/String;)V", false);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
				mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);
				break;

			case URL:

				mv.visitTypeInsn(NEW, "java/net/URL");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(index++); // increases index in array
				mv.visitInsn(AALOAD);
				mv.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);

				break;

			case FILE:

				mv.visitTypeInsn(NEW, "java/io/File");
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitLdcInsn(index++); // increases index in array
				mv.visitInsn(AALOAD);
				mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
				mv.visitFieldInsn(PUTFIELD, className, fieldName, fieldDesc);
				break;
			default:
					//throw internal error (should not be reached
		}

		paramDecs.add(paramDec);

		/********/
//		symtab.insert(paramDec.getIdent().getText(), paramDec);
//		slots.insert(paramDec.getIdent().getText(), paramDec);
		return null;
	}

	@Override // Good
	public Object visitBlock(Block block, Object arg) throws Exception {

		System.out.println(" ---  visitBlock ---- ");


		Label l0 = new Label();
		mv.visitLabel(l0);

		Label l1 = new Label();

		for (Dec dec : block.getDecs()) {
			labels.add(l0);
			labels.add(l1);
			dec.visit(this, mv);
		}

		for (Statement statement : block.getStatements()) {
			statement.visit(this, mv);
			if(statement instanceof BinaryChain){
				mv.visitInsn(POP);
			}
		}

		mv.visitLabel(l1);
		//Pop values from chains if found

		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		System.out.println(" ---  visitDec ---- ");

		LocalAttribute localAttribute = new LocalAttribute();

		localAttribute.ident = declaration.getIdent().getText();
		localAttribute.slotNumber = slot;
		localAttribute.startLabel = labels.get(0);
		localAttribute.endLabel = labels.get(1);
		localAttribute.dec = declaration;
		localVars.add(localAttribute);

		Label start = new Label();
//		Label l1 = new Label();
		mv.visitLabel(start);

//		Label l2= new Label();

		declaration.setSlot(slot);
		declaration.setStartLabel(labels.get(0));
		declaration.setEndLabel(labels.get(1));
		declaration.setDec(declaration);
		decs.add(declaration);

		if(declaration.getTypeName() == TypeName.IMAGE){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlot());
		}

		if(declaration.getTypeName() == TypeName.FRAME){
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, declaration.getSlot());
		}

		slot++;
		Label end = new Label();
		mv.visitLabel(end);
		return null;
	}

	public Dec theDec(String string){
		Dec dec = null;
		for (LocalAttribute var : localVars){
			if (var.dec.getIdent().getText().equals(string)) {
				dec = var.dec;
				return dec;
			}
		}
		for (Dec decker : paramDecs){
			if (decker.getIdent().getText().equals(string)){
				dec = decker;
				return dec;
			}
		}
		return null;
	}

	/***************************************** Statements ***********************************************/

	@Override // Good
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {

		System.out.println(" ---  visitAssignmentStatement ---- ");

		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName()); // changed from getType to getTypeName
		assignStatement.getVar().visit(this, arg);
		if (theDec(assignStatement.getFirstToken().getText()).getTypeName() == IMAGE){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {

		System.out.println(" ---  visitWhileStatement ---- ");

		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitLabel(l1);
		mv.visitJumpInsn(GOTO, l2);

		Label inside = new Label();
		mv.visitLabel(inside);
		whileStatement.getB().visit(this, mv);

		mv.visitLabel(l2);
		whileStatement.getE().visit(this, mv);
		mv.visitJumpInsn(IFNE, inside);

		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {

		System.out.println(" ---  visitIfStatement ---- ");

		ifStatement.getE().visit(this, mv);
		Label f = new Label();
		mv.visitJumpInsn(IFEQ, f);
		Label inside = new Label();
		mv.visitLabel(inside);
		ifStatement.getB().visit(this, mv);
		mv.visitLabel(f);

		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {

		System.out.println(" ---  visitSleepStatement ---- ");

		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	/***************************************** Chains  **************************************************/

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {

		System.out.println(" ---  visitImageOpChain ---- ");

		switch (imageOpChain.getFirstToken().kind) {
			case KW_SCALE:
				Tuple tuple = imageOpChain.getArg();
				Expression expression = tuple.getExprList().get(0);
				expression.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
			break;

			case OP_HEIGHT:
				mv.visitMethodInsn(INVOKEVIRTUAL, " java/awt/image/BufferedImage", "getHeight", PLPRuntimeImageOps.getHeightSig, false);
			break;

			case OP_WIDTH:
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", PLPRuntimeImageOps.getWidthSig, false);
			break;
		}
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {

		System.out.println(" ---  visitFilterOpChain ---- ");

		String fieldName = null;
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, arg);

		mv.visitInsn(ACONST_NULL);

		switch (filterOpChain.getFirstToken().kind) {
			case OP_BLUR:
				fieldName = "blurOp";
				break;
			case OP_CONVOLVE:
				fieldName = "convolveOp";
				break;
			case OP_GRAY:
				fieldName = "grayOp";
			break;
		}
		mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName,fieldName, PLPRuntimeFilterOps.opSig, false);
		mv.visitInsn(DUP);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {

		System.out.println(" ---  visitFrameOpChain ---- ");

		className = PLPRuntimeFrame.JVMClassName;

		Tuple tuple = frameOpChain.getArg();
		tuple.visit(this, arg);

		switch (frameOpChain.getFirstToken().kind) {
			case KW_MOVE:
				mv.visitMethodInsn(INVOKEVIRTUAL, className, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
				break;
			case KW_XLOC:
				mv.visitMethodInsn(INVOKEVIRTUAL, className, "getXVal", PLPRuntimeFrame.getXValDesc, false);
				break;
			case KW_YLOC:
				mv.visitMethodInsn(INVOKEVIRTUAL, className, "getYVal", PLPRuntimeFrame.getYValDesc, false);
				break;
			case KW_SHOW:
				mv.visitMethodInsn(INVOKEVIRTUAL, className, "showImage", PLPRuntimeFrame.showImageDesc, false);
				break;
			case KW_HIDE:
				mv.visitMethodInsn(INVOKEVIRTUAL, className, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
				break;
		}
		mv.visitInsn(DUP);

		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {

		System.out.println(" ---  visitIdentChain ---- ");

		String pos = (String) arg;
		TypeName typeName = identChain.getTypeName();

//		System.out.println(typeName);

		Dec dec = theDec(identChain.getFirstToken().getText()); //?

//		System.out.println(dec.getSlot());

		String fieldName = identChain.getFirstToken().getText();
		String fieldDesc = identChain.getTypeName().getJVMTypeDesc();

		if(pos=="left"){
			switch(typeName){
				case FRAME: case IMAGE:
//					System.out.println(" ---  001 ---- ");
					mv.visitVarInsn(ALOAD, dec.getSlot());
					break;

				case URL:case FILE:
//					System.out.println(" ---  002 ---- ");
					mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldDesc);
					break;

				case INTEGER:case BOOLEAN:
//					System.out.println(" ---  003 ---- ");
					if(dec instanceof ParamDec){
						mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldDesc);
					} else{
						mv.visitVarInsn(ILOAD, dec.getSlot());
					}break;
				default:
					break;
			}
		}
		else {
			switch (typeName) {
				case INTEGER:
//					System.out.println(" ---  004 ---- ");
					if (dec instanceof ParamDec) {
						mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldDesc);
					} else {
						mv.visitVarInsn(ISTORE, dec.getSlot());
					}
					break;

				case IMAGE:
//					System.out.println(" ---  005 ---- ");
					mv.visitVarInsn(ASTORE, dec.getSlot());
					break;

				case FILE:
//					System.out.println(" ---  006 ---- ");
					mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldDesc);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.BufferedImageClassName, "write", PLPRuntimeImageIO.writeImageDesc, false);
					break;

				case FRAME:
//					System.out.println(" ---  007 ---- ");
					mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, dec.getSlot());
					break;

				default:
					break;
			}
		}
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

		System.out.println(" ---  visitBinaryChain ---- ");

		Type.TypeName type0 = binaryChain.getE0().getTypeName();
		Kind opKind = binaryChain.getArrow().kind;
		Type.TypeName type1 = binaryChain.getE1().getTypeName();

		String owner = PLPRuntimeImageIO.className;

		binaryChain.getE0().visit(this, "left");
		if(type0==TypeName.URL){
			mv.visitMethodInsn(INVOKESTATIC, owner, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		}
		else if(type0==TypeName.FILE){
			mv.visitMethodInsn(INVOKESTATIC, owner, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}

		binaryChain.getE1().visit(this, "right");
		if(binaryChain.getE1().getTypeName()==IMAGE){

		}

		if(type0==IMAGE && type1== IMAGE && opKind==Kind.BARARROW){
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESTATIC,"PLPRuntimeImageOps","copyImage",PLPRuntimeImageOps.copyImageSig,false);
			mv.visitInsn(SWAP);
		}
		return null;
	}

	/***************************************** Expressions **********************************************/

	/**
	 * Visit children to generate code to leave values of arguments on stack
	       perform operation, leaving result on top of the stack.  Expressions should
	       be evaluated from left to write consistent with the structure of the AST.
	 * @param binaryExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {

		System.out.println(" ---  visitBinaryExpression ---- ");

		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);

		TypeName typeName0 = binaryExpression.getE0().getTypeName();
		TypeName typeName1 = binaryExpression.getE1().getTypeName();
		Kind opKind = binaryExpression.getOp().kind;

		String owner = PLPRuntimeImageOps.JVMName;

		if(typeName0.isType(IMAGE) && typeName1.isType(IMAGE)){
			switch(opKind){
				case PLUS://load the reference
					mv.visitMethodInsn(INVOKESTATIC, owner, "add", PLPRuntimeImageOps.addSig, false);
					break;
				case MINUS:
					mv.visitMethodInsn(INVOKESTATIC, owner, "sub", PLPRuntimeImageOps.subSig, false);
					break;
			}
		}
		if (typeName0 == TypeName.INTEGER && typeName1 == TypeName.INTEGER) {
			switch (opKind) {
				/*******strongOps********/
				case TIMES:
					mv.visitInsn(IMUL);
					break;
				case DIV:
					mv.visitInsn(IDIV);
					break;
				case MOD:
					mv.visitInsn(IREM);
					break;
				case AND:
					mv.visitInsn(IAND);
					break;
				/*******WeakOps********/
				case PLUS:
					mv.visitInsn(IADD);
					break;
				case MINUS:
					mv.visitInsn(ISUB);
					break;
				case OR:
					mv.visitInsn(IOR);
					break;
				/*******relOps********/
				case LT:
					Label f = new Label();
					mv.visitJumpInsn(IF_ICMPGE, f);
					mv.visitInsn(ICONST_1);
					Label t = new Label();
					mv.visitJumpInsn(GOTO, t);
					mv.visitLabel(f);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(t);
					break;
				case GT:
					Label gt0 = new Label();
					mv.visitJumpInsn(IF_ICMPLE, gt0);
					mv.visitInsn(ICONST_1);
					Label gt1 = new Label();
					mv.visitJumpInsn(GOTO, gt1);
					mv.visitLabel(gt0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(gt1);
					break;
				case LE:
					Label f6 = new Label();
					mv.visitJumpInsn(IF_ICMPGT, f6);
					mv.visitInsn(ICONST_1);
					Label t6 = new Label();
					mv.visitJumpInsn(GOTO, t6);
					mv.visitLabel(f6);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(t6);
					break;

				case GE:
					Label ge0 = new Label();
					mv.visitJumpInsn(IF_ICMPLT, ge0);
					mv.visitInsn(ICONST_1);
					Label ge1 = new Label();
					mv.visitJumpInsn(GOTO, ge1);
					mv.visitLabel(ge0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(ge1);
					break;

				case NOTEQUAL:
					Label ne0 = new Label();
					mv.visitJumpInsn(IF_ICMPEQ, ne0);
					mv.visitInsn(ICONST_1);
					Label ne1 = new Label();
					mv.visitJumpInsn(GOTO, ne1);
					mv.visitLabel(ne0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(ne1);
					break;

				case EQUAL:
					Label f0 = new Label();
					mv.visitJumpInsn(IF_ICMPNE, f0);
					mv.visitInsn(ICONST_1);
					Label f1 = new Label();
					mv.visitJumpInsn(GOTO, f1);
					mv.visitLabel(f0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(f1);
					break;
			}
		}
		if((typeName0 == IMAGE && typeName1 == TypeName.INTEGER)
				||(typeName0 == TypeName.INTEGER && typeName1==TypeName.IMAGE)){

		if(typeName0 == TypeName.INTEGER){
			mv.visitInsn(SWAP);
		}
		switch (opKind) {
			case TIMES:
				mv.visitMethodInsn(INVOKESTATIC, owner, "mul",PLPRuntimeImageOps.modSig, false);
				break;
			case MOD:
				mv.visitMethodInsn(INVOKESTATIC, owner, "mod",PLPRuntimeImageOps.modSig, false);
				break;
			case DIV:
				mv.visitMethodInsn(INVOKESTATIC, owner, "div",PLPRuntimeImageOps.divSig, false);
				break;
			case MINUS:
				mv.visitMethodInsn(INVOKESTATIC, owner, "sub",PLPRuntimeImageOps.subSig, false);
				break;
		}
		if (typeName0 == BOOLEAN && typeName1 == BOOLEAN) {
			switch (opKind) {
				case LT:
					Label f = new Label();
					mv.visitJumpInsn(IF_ICMPGE, f);
					mv.visitInsn(ICONST_1);
					Label t = new Label();
					mv.visitJumpInsn(GOTO, t);
					mv.visitLabel(f);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(t);
					break;

				case GT:
					Label gt0 = new Label();
					mv.visitJumpInsn(IF_ICMPLE, gt0);
					mv.visitInsn(ICONST_1);
					Label gt1 = new Label();
					mv.visitJumpInsn(GOTO, gt1);
					mv.visitLabel(gt0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(gt1);
					break;

				case LE:
					Label f6 = new Label();
					mv.visitJumpInsn(IF_ICMPGT, f6);
					mv.visitInsn(ICONST_1);
					Label t6 = new Label();
					mv.visitJumpInsn(GOTO, t6);
					mv.visitLabel(f6);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(t6);
					break;

				case GE:
					Label ge0 = new Label();
					mv.visitJumpInsn(IF_ICMPLT, ge0);
					mv.visitInsn(ICONST_1);
					Label ge1 = new Label();
					mv.visitJumpInsn(GOTO, ge1);
					mv.visitLabel(ge0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(ge1);
					break;

				case NOTEQUAL:
					Label ne0 = new Label();
					mv.visitJumpInsn(IF_ICMPEQ, ne0);
					mv.visitInsn(ICONST_1);
					Label ne1 = new Label();
					mv.visitJumpInsn(GOTO, ne1);
					mv.visitLabel(ne0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(ne1);
					break;

				case EQUAL:
					Label f0 = new Label();
					mv.visitJumpInsn(IF_ICMPNE, f0);
					mv.visitInsn(ICONST_1);
					Label f1 = new Label();
					mv.visitJumpInsn(GOTO, f1);
					mv.visitLabel(f0);
					mv.visitInsn(ICONST_0);
					mv.visitLabel(f1);
					break;
				}
			}
		}
		return null;
	}

	/**
	 * loads value of variable to the top of the stack (this could be a field or a local var)
	 * @param identExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {

		System.out.println(" ---  visitIdentExpression ---- ");

		String fieldName = identExpression.getFirstToken().getText();
		String fieldDesc = identExpression.getTypeName().getJVMTypeDesc();

//		Dec dec = slots.lookup(identExpression.getFirstToken().getText());
//
//		if (dec instanceof ParamDec) {
//			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), identExpression.getTypeName().getJVMTypeDesc());
//		} else {
//			slot = slots.getSlot(identExpression.getFirstToken().getText());
//			if (identExpression.getTypeName() == BOOLEAN || identExpression.getTypeName() == TypeName.INTEGER) {
//				mv.visitVarInsn(ILOAD, slot);
//			}
//		}

		Dec dec = theDec(identExpression.getFirstToken().getText());

		TypeName typeName = dec.getTypeName();

		if (dec instanceof ParamDec) {
			mv.visitFieldInsn(GETSTATIC, className, fieldName, fieldDesc);
		}
		else if (typeName == TypeName.INTEGER || typeName == TypeName.BOOLEAN) {
			mv.visitVarInsn(ILOAD, dec.getSlot());
		} else {
			mv.visitVarInsn(ALOAD, dec.getSlot());
		}
		return null;
	}

	/**
	 * store the value that is on top of stack to this variable (which could be a field or local var)
	 * @param identX
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {

		System.out.println(" ---  visitIdentLValue ---- ");

		String fieldName = identX.getFirstToken().getText();

//		System.out.println("\n \t We are here at 00 now\n");

		Dec dec = theDec(identX.getText());

//		System.out.println("\n \t We are here at 01 now\n");

		TypeName indexType = dec.getTypeName();

//		System.out.println("\n \t We are here at 02 now\n");

		if (dec instanceof ParamDec){
			System.out.println("\n \t We are here at paramDec now\n");
			mv.visitFieldInsn(PUTFIELD, className, fieldName, dec.getTypeName().getJVMTypeDesc());
		} else if (indexType == TypeName.INTEGER || indexType == TypeName.BOOLEAN){
			System.out.println("\n \t We are here at 03 now\n");
			mv.visitVarInsn(ISTORE, dec.getSlot());
			System.out.println("\n \t We are here at 04 now\n");
		} else if (indexType == TypeName.IMAGE){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
			mv.visitVarInsn(ASTORE, dec.getSlot());
		} else {
			mv.visitVarInsn(ASTORE, dec.getSlot());
		}

		Label identXStore = new Label();
		mv.visitLabel(identXStore);
		System.out.println("\t end of LValue");
		return null;
	}

	/**
	 * load booleanLiteral value
	 * @param booleanLitExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override // Good
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {

		System.out.println(" ---  visitBooleanLitExpression ---- ");

		if (booleanLitExpression.getValue()){
			mv.visitInsn(ICONST_1);
		} else {
			mv.visitInsn(ICONST_0);
		}
		return null;
	}

	/**
	 * load intLiteral value
	 * @param intLitExpression
	 * @param arg
	 * @return
	 * @throws Exception
	 */
	@Override // Good
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {

		System.out.println(" ---  visitIntLitExpression ---- ");

		mv.visitLdcInsn(new Integer(intLitExpression.value));
		return null;
	}

	@Override // Good
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {

		System.out.println(" ---  visitConstantExpression ---- ");

		className = PLPRuntimeFrame.JVMClassName;

		if(constantExpression.getFirstToken().kind == KW_SCREENHEIGHT){
			mv.visitMethodInsn(INVOKESTATIC, className,"getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		if(constantExpression.getFirstToken().kind == KW_SCREENWIDTH){
			mv.visitMethodInsn(INVOKESTATIC, className,"getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		}
		return null;
	}

	/************************************* IdentLVal and Tuple*******************************************/

	@Override // Good
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		System.out.println(" ---  visitTuple ---- ");

		// Visit expressions to generate code to leave values on top of the stack
		for (Expression expression : tuple.getExprList()){
			expression.visit(this, arg); // check with mv
		}
		return null;
	}

	/******************************** End of File  ******************************************************/
}