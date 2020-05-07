package cop5556sp17;

import cop5556sp17.Scanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScannerTestExceptions {
    String input;
    String text;
    Scanner scanner;
    Scanner.Token token;

    @Test
    public void testInCommentException() throws Scanner.IllegalCharException, Scanner.IllegalNumberException {
        input = "/*";
        scanner = new Scanner(input);
        assertThrows(Scanner.IllegalCharException.class, scanner::scan);
    }

    @Test
    public void testUncommentedException() throws Scanner.IllegalCharException, Scanner.IllegalNumberException {
        input = "/*/";
        scanner = new Scanner(input);
        assertThrows(Scanner.IllegalCharException.class, scanner::scan);
    }


    @Test
    public void testIntOverflowError() throws Scanner.IllegalCharException, Scanner.IllegalNumberException {
        input = "99999999999999999";
        scanner = new Scanner(input);
        assertThrows(Scanner.IllegalNumberException.class, scanner::scan);
    }

}