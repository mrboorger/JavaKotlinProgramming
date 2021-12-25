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
        testComputeExpression();
        testToString();
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

        TesterImpl.parserExceptionTester.test(parser, "* 2 2");
        TesterImpl.parserExceptionTester.test(parser, "/ 2 2");
        TesterImpl.parserExceptionTester.test(parser, "/ 2 + 2");
        TesterImpl.parserExceptionTester.test(parser, "2 / - 2");

        TesterImpl.parserExceptionTester.test(parser, "(");
        TesterImpl.parserExceptionTester.test(parser, ")");
        TesterImpl.parserExceptionTester.test(parser, "()");
        TesterImpl.parserExceptionTester.test(parser, "(1 + (2 + 3)))");
        TesterImpl.parserExceptionTester.test(parser, "((1+2)");
    }

    private static class parserDebugRepresentationTester {
        private static void test(ParserImpl parser, String strExpr, String expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert false : "Unexpected ExpressionParseException on " + strExpr + e.getMessage();
            }

            String result = root.accept(DebugRepresentationExpressionVisitor.INSTANCE);
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

        {
            String strExpr = "1 * 3";
            String expected = "MUL(CON[1.0], CON[3.0])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "1.2 * 3.3";
            String expected = "MUL(CON[1.2], CON[3.3])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x + x / 3 * 2 + 3";
            String expected = "ADD(ADD(VAR[x], MUL(DIV(VAR[x], CON[3.0]), CON[2.0])), CON[3.0])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "x + x / 3 * 2 + 3";
            String expected = "ADD(ADD(VAR[x], MUL(DIV(VAR[x], CON[3.0]), CON[2.0])), CON[3.0])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "2 * x + 3 / 4.2 + t";
            String expected = "ADD(ADD(MUL(CON[2.0], VAR[x]), DIV(CON[3.0], CON[4.2])), VAR[t])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "  (1)   ";
            String expected = "PAR(CON[1.0])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "  (1 )";
            String expected = "PAR(CON[1.0])";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "( 1 + 2)";
            String expected = "PAR(ADD(CON[1.0], CON[2.0]))";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "(x + 13) / ((1 - xx) * 3)";
            String expected = "DIV(PAR(ADD(VAR[x], CON[13.0])), PAR(MUL(PAR(SUB(CON[1.0], VAR[xx])), CON[3.0])))";
            parserDebugRepresentationTester.test(parser, strExpr, expected);
        }
    }

    private static class parserCalcDepthTester {
        private static void test(ParserImpl parser, String strExpr, Integer expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert false : "Unexpected ExpressionParseException on " + strExpr;
            }

            Integer result = root.accept(CalcDepthVisitor.INSTANCE);
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
            Integer expected = 100001;
            parserCalcDepthTester.test(parser, "x" + " + x".repeat(100000), expected);
        }
        {
            String strExpr = "x + x / 3 * 2 + 3";
            Integer expected = 5;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "2 * 2 + 3 * 3";
            Integer expected = 3;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "(Chifuyu)";
            Integer expected = 2;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "  (((1)))   +   2  ";
            Integer expected = 5;
            parserCalcDepthTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "    3 + (Chifuyu + 33) / ((13 - x) + 12)  ";
            Integer expected = 7;
            parserCalcDepthTester.test(parser, strExpr, expected);
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
                assert false : "Unexpected ExpressionParseException on " + strExpr;
            }

            var varVisitor = new VariablesVisitor(scanner);

            root.accept(varVisitor);
            try {
                Field field = varVisitor.getClass().getDeclaredField("mVariablesValues");
                field.setAccessible(true);
                var hashMap = (HashMap<String, Integer>)field.get(varVisitor);
                assert hashMap.equals(expected) : "HashMaps don't equals";
            } catch (NoSuchFieldException | IllegalAccessException e) {
                assert false : "No field error";
            }
        }
    }

    void testVariablesVisitor() {
        var parser = new ParserImpl();
        {
            var strExpr = "Sd";
            var scanner = new Scanner("3.1\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("Sd", 3.1);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            var strExpr = "x + 1.2 + x";
            var scanner = new Scanner("3.33\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("x", 3.33);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            var strExpr = "x + 1.2 + x + xx - x + xx";
            var scanner = new Scanner("3.33 -1.23\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("x", 3.33);
                put("xx", -1.23);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            var strExpr = "12 + x / 3 - xx * 3 / x";
            var scanner = new Scanner("3.33 4.33\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("x", 3.33);
                put("xx", 4.33);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
        {
            String strExpr = "3 + (Chifuyu + 33) / ((13 - x) + 12)";
            var scanner = new Scanner("3.33 4.33\n").useLocale(Locale.US);
            var expected = new HashMap<String, Double>() {{
                put("Chifuyu", 3.33);
                put("x", 4.33);
            }};
            variablesVisitorTester.test(parser, strExpr, scanner, expected);
        }
    }

    private static class ComputeExpressionTester {
        static final double EPS = 1e-9;

        private static void test(ParserImpl parser, String strExpr,
                                 Scanner scanner, double expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert false : "Unexpected ExpressionParseException on " + strExpr;
            }

            var varVisitor = new VariablesVisitor(scanner);
            root.accept(varVisitor);

            var result = (double)root.accept(ComputeExpressionVisitor.INSTANCE);
            assert Math.abs(result - expected) < EPS : "Invalid result at expression " + strExpr +
                                        " [" + result + " vs " + expected + "]";
        }
    }

    private static void testComputeExpression() {
        var parser = new ParserImpl();
        {
            var strExpr = "-0.233";
            var scanner = new Scanner("\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, -0.233);
        }
        {
            var strExpr = "-0.233 + 13";
            var scanner = new Scanner("\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 12.767);
        }
        {
            var strExpr = "x";
            var scanner = new Scanner("3.1\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 3.1);
        }
        {
            var strExpr = "1 - x";
            var scanner = new Scanner("-1.1\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 2.1);
        }
        {
            var strExpr = "x + 1.3 + x - x";
            var scanner = new Scanner("3.1\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 4.4);
        }
        {
            var strExpr = "x + 1.3 + x - x - xx";
            var scanner = new Scanner("3.1 2.2\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 2.2);
        }
        {
            var strExpr = "3 * 5.3";
            var scanner = new Scanner("\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 15.9);
        }
        {
            var strExpr = "5 / 4";
            var scanner = new Scanner("\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 1.25);
        }
        {
            var strExpr = "2.5 * x / 10 - 3.0 / x";
            var scanner = new Scanner("5.5\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 0.829545454545);
        }
        {
            var strExpr = "2.5 * x / 10 - 3.0 / x + xx / x - x";
            var scanner = new Scanner("3.2 4\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, -2.0875);
        }
        {
            String strExpr = "3 + (Chifuyu + 33) / ((13 - x) + 12)";
            var scanner = new Scanner("3.33 4.33\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, 4.75761973875);
        }
        {
            String strExpr = "(12 - 3) / x + x - 3 * ((xX + -1.2) * 2.5)";
            var scanner = new Scanner("5 3\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, -6.7);
        }
        {
            String strExpr = "3 + 4 * (-4 - (-4 * ((3 + 4) - 5) / (12 + 4 / 6)) / 6) * 5";
            var scanner = new Scanner("3 4 -4\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, -74.894736842105263);
        }
        {
            String strExpr = "    gf + t * (x - (x * ((gf + t) - 5) / (12 + 4 / 6)) / 6) * 5 ";
            var scanner = new Scanner("3 4 -4\n").useLocale(Locale.US);
            ComputeExpressionTester.test(parser, strExpr, scanner, -74.894736842105263);
        }
    }

    private static class ToStringTester {
        private static void test(ParserImpl parser, String strExpr, String expected) {
            Expression root = null;
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                assert false : "Unexpected ExpressionParseException on " + strExpr;
            }

            var result = (String)root.accept(ToStringVisitor.INSTANCE);
            assert result.equals(expected) : "Not same representation " + strExpr +
                    "[" + result + " vs " + expected + "]";
        }
    }

    private static void testToString() {
        var parser = new ParserImpl();
        {
            var strExpr = "  -0.233 ";
            var expected = "-0.233";
            ToStringTester.test(parser, strExpr, expected);
        }
        {
            var strExpr = "    (Budgie )";
            var expected = "(Budgie)";
            ToStringTester.test(parser, strExpr, expected);
        }
        {
            var strExpr = "1 +   3 ";
            var expected = "1.0 + 3.0";
            ToStringTester.test(parser, strExpr, expected);
        }
        {
            var strExpr = "x / x + 13 - (3 )";
            var expected = "x / x + 13.0 - (3.0)";
            ToStringTester.test(parser, strExpr, expected);
        }
        {
            String strExpr = "3 + (Chifuyu + 33) / ((13 - x) + 12)";
            var expected = "3.0 + (Chifuyu + 33.0) / ((13.0 - x) + 12.0)";
            ToStringTester.test(parser, strExpr, expected);
        }
        {
            var strExpr = "    gf +   t * (x - (x *   ((gf + t) - 5    ) / " +
                          "(12   +  4  /   6)) / 6)   *   5   ";
            var expected = "gf + t * (x - (x * ((gf + t) - 5.0) / (12.0 + 4.0 / 6.0)) / 6.0) * 5.0";
            ToStringTester.test(parser, strExpr, expected);
        }
    }
}
