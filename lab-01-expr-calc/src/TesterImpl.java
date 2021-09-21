public class TesterImpl implements Tester {
    @Override
    public void runAllTests() {
        testParserException();
        testDebugRepresentation();
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

        TesterImpl.parserExceptionTester.test(parser, "2 2");
        TesterImpl.parserExceptionTester.test(parser, "2 + 2 3");
        TesterImpl.parserExceptionTester.test(parser, "2 +");
        TesterImpl.parserExceptionTester.test(parser, "+");

        TesterImpl.parserExceptionTester.test(parser, "+ 2 2");
        TesterImpl.parserExceptionTester.test(parser, "- 1 2");
        TesterImpl.parserExceptionTester.test(parser, "1 + 2 - -");

        TesterImpl.parserExceptionTester.test(parser, "\n 1 - 2  - -");
        TesterImpl.parserExceptionTester.test(parser, "\n 1 - - 3 3");
        TesterImpl.parserExceptionTester.test(parser, "2 + 2 + xssd_dwD -   fg   -");
    }



    private static void testDebugRepresentation() {
        var parser = new ParserImpl();
        Expression root = null;
        var strExpr = "-1 - 2 + 3.123 + Xsd + sd0dd - 0.002333033";
        try {
            root = parser.parseExpression(strExpr);
        } catch (ExpressionParseException e) {
            assert true : "Unexpected ExpressionParseException on " + strExpr + e.getMessage();
        }
        assert root != null : "Unexpected null root";

        System.out.println(root.accept(new DebugRepresentationExpressionVisitor()));
    }
}
