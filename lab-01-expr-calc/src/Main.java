import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
        // TODO: rename method with lowerCamelCase
        testParserException();
    }

    interface exceptionTester {
        void test(ParserImpl parser, String strExpr);
    }

    private static class parserExceptionTester {
        private static void test(ParserImpl parser, String strExpr) {
            boolean is_caught = false;
            try {
                parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                is_caught = true;
            }
            assert is_caught : "parser hasn't thrown the exception on expression " + strExpr;
        }
    }

    private static void testParserException() {
        ParserImpl parser = new ParserImpl();

        parserExceptionTester.test(parser, "2 2");
        parserExceptionTester.test(parser, "2 + 2 3");
        parserExceptionTester.test(parser, "2 +");
        parserExceptionTester.test(parser, "+");
        parserExceptionTester.test(parser, "+ 2 2");
        parserExceptionTester.test(parser, "\n 1 - 2  - -");
        parserExceptionTester.test(parser, "\n 1 - - 3 3");
        parserExceptionTester.test(parser, "2 + 2 + xssd_dwD -   fg   -");
    }
}
