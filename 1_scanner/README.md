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

##### ident ::= [ident_start](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#ident_start---a--z--a--z----_-)  [ident_part](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#ident_part--ident_start----0--9--)*    (but not reserved tokens)<br>
##### token ::= [ident](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#ident--ident_start--ident_part----but-not-reserved-tokens) | [keyword](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#keyword--integer--boolean--image--url--file--frame--while--if--sleep--screenheight--screenwidth-) | [frame_op_keyword](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#frame_op_keyword--xloc--yloc--hide--show--move-) | [filter_op_keyword](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#filter_op_keyword--gray--convolve--blur--scale-) | [image_op_keyword](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#image_op_keyword--width--height-) | [boolean_literal](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#boolean_literal--true--false-) | [int_literal](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#int_literal--0--19-09-) | [separator](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#separator-------------) | [operator](https://github.com/jodth07/cop/blob/master/1_scanner/README.md#operator-----------------------------------------) <br>

### Expected Exceptions handling 
● If an illegal character is encountered, your scanner should throw an IllegalCharException. The message should contain useful information about the error<br>  
● If an integer literal is provided that is out of the range of a Java int, then your scanner should throw an IllegalNumberException.
