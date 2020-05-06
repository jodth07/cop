# Scanner implementation instructions and requirements using Backus-Naur Form Syntax

## Implement a recursive descent parser for the following context-free grammar:

##### filterOp ::= **`OP_BLUR`** | **`OP_GRAY`** | **`OP_CONVOLVE`**
##### frameOp ::= **`KW_SHOW`** | **`KW_HIDE`** | **`KW_MOVE`** | **`KW_XLOC`** | **`KW_YLOC`**
##### imageOp ::= **`OP_WIDTH`** | **`OP_HEIGHT`** | **`KW_SCALE`**
##### arrowOp ∷= **`ARROW`** | **`BARARROW`**
##### relOp ∷=  **`LT`** | **`LE`** | **`GT`** | **`GE`** | **`EQUAL`** | **`NOTEQUAL`** 
##### weakOp  ∷= **`PLUS`** | **`MINUS`** | **`OR`**
##### strongOp ∷= **`TIMES`** | **`DIV`** | **`AND`** | **`MOD`**     

##### dec ::= ( **`KW_INTEGER`** | **`KW_BOOLEAN`** | **`KW_IMAGE`** | **`KW_FRAME`** )    **`IDENT`**
##### paramDec ::= ( **`KW_URL`** | **`KW_FILE`** | **`KW_INTEGER`** | **`KW_BOOLEAN`** )   **`IDENT`**

##### factor ∷= **`IDENT`** | **`INT_LIT`** | **`KW_TRUE`** | **`KW_FALSE`** | **`KW_SCREENWIDTH`** | **`KW_SCREENHEIGHT`** | **`(`** **`expression`** **`)`**
##### elem ∷= **`factor`** ( **`strongOp`** **`factor`** )*
##### term ∷= **`elem`** ( **`weakOp`**  **`elem`** )*
##### expression ∷= **`term`** ( **`relOp`** **`term`** )*

##### arg ::= **`ε`** | **`(`** **`expression`** ( **`,`** **`expression`** )* **`)`**
##### chainElem ::= **`IDENT`** | **`filterOp arg`** | **`frameOp arg`** | **`imageOp`** **`arg`**
##### chain ::=  **`chainElem`** **`arrowOp`** **`chainElem`** ( **`arrowOp`**  **`chainElem`** )*
##### assign ::= **`IDENT`** **`ASSIGN`** **`expression`**
##### statement ::=   **`OP_SLEEP`** **`expression`** **`;`** | **`whileStatement`** | **`ifStatement`** | **`chain ;`** | **`assign`** **`;`**

##### whileStatement ::= **`KW_WHILE`** **`(`** **`expression`** **`)`** **`block`**
##### ifStatement ::= **`KW_IF`** **`(`** **`expression`** **`)`** **`block`**
##### block ::= **`{`** ( **`dec`** | **`statement`** ) * **`}`**

##### program ::=  **`IDENT`** **`block`**
##### program ::=  **`IDENT`** **`param_dec`** ( **`,`** **`param_dec`** )*   **`block`**

### Expected Exceptions handling 
●	The parser should simply determine whether the given sentence is legal or not.  If not, the parser should throw a SyntaxException.  If the sentence is legal, the parse method should simply return.<br> 
●	Use the approach described in the lectures to systematically build the parser.  Identify whether the language is LL(1) or not.
