public class TesterImpl implements Tester {
    @Override
    public void runAllTests() {
        testParserException();
        testDebugRepresentation();
        testCalcDepth();
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
            parserDebugRepresentationTester.test(parser,strExpr, expected);
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
}
