# Scanner implementation instructions and requirements

## Implement a scanner for the programming language with the following lexical structure

### Tokens extractions
##### comment ::=   **`/*`**  NOT(\*/)\*  **`*/`** <br>
##### int_literal ::= **`0`** | **`(1..9)`** **`(0..9)*`** <br>
##### keyword ::= **`integer`** | **`boolean`** | **`image`** | **`url`** | **`file`** | **`frame`** | **`while`** | **`if`** | **`sleep`** | **`screenheight`** | **`screenwidth`** <br> 
##### filter_op_keyword ∷= **`gray`** | **`convolve`** | **`blur`** | **`scale`** <br>
##### image_op_keyword ∷= **`width`** | **`height`** <br>
##### frame_op_keyword ∷= **`xloc`** | **`yloc`** | **`hide`** | **`show`** | **`move`** <br>
##### boolean_literal ::= **`true`** | **`false`** <br>
##### separator ::= **`;`** | **`,`** | **`(`** | **`)`** | **`{`** | **`}`** <br>
##### operator ::=   	**`|`** | **`&`** | **`==`** | **`!=`** | **`<`** | **`>`** | **`<=`** | **`>=`** | **`+`** | **`-`** | **`*`** | **`/`** | **`%`** | **`!`** | **`->`** | **`|->`** | **`<-`** <br>
##### ident_start ::=  **`A .. Z`** | **`a .. z`** | **`$`** | **`_`** <br>
##### ident_part ::= [ident_start](https://github.com/jodth07/cop/tree/master/1_scanner#ident_start---a--z--a--z----_-)  | **`( 0 .. 9 )`** <br>

##### ident ::= **`ident_start`**  **`ident_part*`**    (but not reserved tokens)<br>
##### token ::= **`ident`** | **`keyword`** | **`frame_op_keyword`** | **`filter_op_keyword`** | **`image_op_keyword`** | **`boolean_literal`** | **`int_literal`** | **`separator`** | **`operator`** <br>

### Expected Exceptions handling 
● If an illegal character is encountered, your scanner should throw an IllegalCharException. The message should contain useful information about the error<br>  
● If an integer literal is provided that is out of the range of a Java int, then your scanner should throw an IllegalNumberException.
