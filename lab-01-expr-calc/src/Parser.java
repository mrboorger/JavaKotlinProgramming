public interface Parser {
    /**
     * Parses expression from the string.
     * If the string doesn't represent a valid expression, then
     *  throws ExpressionParseException.
     *  All binary operators must be separated by spaces.
     *  Variables must start with english letter.
     *
     * @param input the input string.
     * @return parsed expression tree.
     */
    Expression parseExpression(String input) throws ExpressionParseException;
}
