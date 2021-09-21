public interface ExpressionVisitor {
    Object visitBinaryExpression(BinaryExpression expr);
    Object visitLiteral(Literal expr);
    // TODO: make extended version
//    Object visitParenthesis(ParenthesisExpression expr);
}
