public class TesterImpl implements Tester {
    @Override
    public void runAllTests() {
        testParserException();
        testDebugRepresentation();
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
}
