import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class TesterImpl implements Tester {
    @Override
    public void runAllTests() {
        testParserException();
        testDebugRepresentation();
        testCalcDepth();
        testVariablesVisitor();
    }

    private static class parserExceptionTester {
        private static void test(ParserImpl parser, String strExpr) {
            boolean isCaught = false;
            try {
                parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                isCaught = true;
            }
            assert isCaught : "parser hasn't thrown the exception on expression " + strExpr;
        }
    }

    private static void testParserException() {
        ParserImpl parser = new ParserImpl();

        TesterImpl.parserExceptionTester.test(parser, "2 2");
        TesterImpl.parserExceptionTester.test(parser, "2 + 2 3");
        TesterImpl.parserExceptionTester.test(parser, "2 +");
        TesterImpl.parserExceptionTester.test(parser, "+");

        TesterImpl.parserExceptionTester.test(parser, "+ 2 2");
        TesterImpl.parserExceptionTester.test(parser, "- 1 2");
        TesterImpl.parserExceptionTester.test(parser, "1 + 2 - - 2 2");

        TesterImpl.parserExceptionTester.test(parser, "\n 1 - 2  - -");
        TesterImpl.parserExceptionTester.test(parser, "\n 1 - - 3 3");
        TesterImpl.parserExceptionTester.test(parser, "2 + 2 + xssd_dwD -   fg   -");

        TesterImpl.parserExceptionTester.test(parser, "1s");
        TesterImpl.parserExceptionTester.test(parser, "5ds + fd");
        TesterImpl.parserExceptionTester.test(parser, "f - -d");
        TesterImpl.parserExceptionTester.test(parser, "f - +d");
        TesterImpl.parserExceptionTester.test(parser, "_dsfsd");
    }

    private static class parserDebugRepresentationTester {
        private static void test(ParserImpl parser, String strExpr, String expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert true : "Unexpected ExpressionParseException on " + strExpr + e.getMessage();
            }
            assert root != null : "Unexpected null root";

            String result = (String)root.accept(new DebugRepresentationExpressionVisitor());
            assert result.equals(expected) : result + "!=" + expected;
        }
    }

    private static void testDebugRepresentation() {
        var parser = new ParserImpl();
        {
            String strExpr = "92.3";
            String expected = "CON[92.3]";
            parserDebugRepresentationTester.test(parser,strExpr, expected);
        }
        {
            String strExpr = "X_d022";
            String expected = "VAR[X_d022]";
            parserDebugRepresentationTester.test(parser,strExpr, expected);
        }
        {
            String strExpr = "X_d022 - 92.3";
            String expected = "SUB(VAR[X_d022], CON[92.3])";
            parserDebugRepresentationTester.test(parser,strExpr, expected);
        }
        {
            String strExpr = "x_w + 2.3 - 0.22";
            String expected = "SUB(ADD(VAR[x_w], CON[2.3]), CON[0.22])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
    }

    private static class parserCalcDepthTester {
        private static void test(ParserImpl parser, String strExpr, Integer expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert true : "Unexpected ExpressionParseException on " + strExpr;
            }
            assert root != null : "Unexpected null root";

            Integer result = (Integer)root.accept(new CalcDepthVisitor());
            assert result.equals(expected) : "Wrong depth " + result + " != " + expected;
        }
    }

    void testCalcDepth() {
        var parser = new ParserImpl();
        {
            String strExpr = "92.3";
            Integer expected = 1;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x";
            Integer expected = 1;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x + 12";
            Integer expected = 2;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x + 12 - 13";
            Integer expected = 3;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x + 12 - 13 - 2";
            Integer expected = 4;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            var strBuilder = new StringBuilder("x");
            for (int i = 0; i < 1000000; ++i) {
                strBuilder.append(" + x");
            }
            Integer expected = 1000001;
            parserCalcDepthTester.test(parser, strBuilder.toString(), expected);
        }
    }

    private static class variablesVisitorTester {
        private static void test(ParserImpl parser, String strExpr,
                                 Scanner scanner,
                                 HashMap<String, Double> expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert true : "Unexpected ExpressionParseException on " + strExpr;
            }
            assert root != null : "Unexpected null root";

            var varVisitor = new VariablesVisitor(scanner);

            root.accept(varVisitor);
            try {
                Field field = varVisitor.getClass().getDeclaredField("mVariablesValues");
                field.setAccessible(true);
                var hashMap = (HashMap<String, Integer>)field.get(varVisitor);
                assert hashMap.equals(expected) : "HashMaps don't equals";
            } catch (NoSuchFieldException | IllegalAccessException e) {
                assert true : "No field error";
            }
        }
    }

    void testVariablesVisitor() {
        var parser = new ParserImpl();
        {
            var strExpr = new String("Sd");
            var scanner = new Scanner("3.1\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("Sd", 3.1);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            var strExpr = new String("x + 1.2 + x");
            var scanner = new Scanner("3.33\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("x", 3.33);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            var strExpr = new String("x + 1.2 + x + xx - x + xx");
            var scanner = new Scanner("3.33 -1.23\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("x", 3.33);
                put("xx", -1.23);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
    }
}
