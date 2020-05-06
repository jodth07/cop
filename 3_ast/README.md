# AST implementation instructions and requirements using Backus-Naur Form Syntax

## Modify your parser to return an abstract syntax tree specified by the following abstract syntax:

##### Program ∷= List<ParamDec> Block
##### ParamDec ∷= type ident
##### Block ∷= List<Dec>  List<Statement>
##### Dec ∷= type ident
##### Statement ∷= SleepStatement | WhileStatement | IfStatement | Chain | AssignmentStatement
##### SleepStatement ∷= Expression
##### AssignmentStatement ∷= IdentLValue Expression
##### Chain ∷= ChainElem | BinaryChain
##### ChainElem ::= IdentChain | FilterOpChain | FrameOpChain | ImageOpChain
##### IdentChain ∷= ident
##### FilterOpChain ∷= filterOp Tuple
##### FrameOpChain ∷= frameOp Tuple
##### ImageOpChain ∷= imageOp Tuple
##### BinaryChain ∷= Chain (arrow | bararrow)  ChainElem
##### WhileStatement ∷= Expression Block
##### IfStatement ∷= Expression Block
##### Expression ∷= IdentExpression | IntLitExpression | BooleanLitExpression | ConstantExpression | BinaryExpression
##### IdentExpression ∷= ident
##### IdentLValue ∷= ident
##### IntLitExpression ∷= intLit
##### BooleanLitExpression ∷= booleanLiteral
##### ConstantExpression ∷= screenWidth | screenHeight
##### BinaryExpression ∷= Expression op Expression
##### Tuple :≔ List<Expression>
##### op ∷= relOp | weakOp | strongOp
##### type ∷= integer | image | frame | file | boolean | url

In this syntax, upper case names correspond to classes representing nodes in the AST.  Lower case names like op or ident
 will be represented by their Tokens.  The correspondence between the concrete and abstract syntax should be clear, 
 except possibly that arg in the concrete syntax from Assignment 2 will correspond to a Tuple here.<br>

The AST classes have been provided for you in the attached jar file.  These classes are instrumented to use with the 
Visitor pattern which will be used in later assignments.  The ASTNode class is the abstract superclass of all the other 
classes provided and has one field, a Token firstToken. This should be set to the first token in the construct of all 
nodes.   In later parts, you can use the firstToken to identify the location in the source code where errors occur.<br>

In some cases there is only one Token inside the firstToken will be the only field.  For example IdentExpression 
contains a single ident which is also the firstToken.  Also, in Program, the program name is stored in firstToken.<br>

In Chain, the  arrow is left associative.<br>  

Although in this assignment, you probably will not need to change any of the provided classes, you will need to in later 
assignments, so to keep our process consistent, include them in this assignment as well. <br>


Comments and suggestions:
●	Work incrementally, starting with small constructs and moving to bigger ones, and adding tests each time. <br>
●	You will want to provide better error messages than given in the sample code.  In particular, 
you will want to output the location of the offending token.<br>
●	To write a junit test, you will need to know the structure of the AST that should be created.  
I recommend using assertEquals(ExpectedClass.class, obj.getClass()); to check that obj is an instance of ExpectedClass 
as this will give you better error messages from JUnit than the alternative assertTrue(obj instanceof ExpectedClass).

