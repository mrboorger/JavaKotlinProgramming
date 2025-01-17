public class DebugRepresentationExpressionVisitor implements ExpressionVisitor<String> {
    private DebugRepresentationExpressionVisitor() {}

    @Override
    public String visitBinaryExpression(BinaryExpression expr) {
        var leftValue = expr.getLeft().accept(this);
        var rightValue = expr.getRight().accept(this);
        return expr.getOperation().getOpName() + "(" + leftValue + ", " + rightValue + ")";
    }

    @Override
    public String visitLiteral(Literal expr) {
        return "CON[" + expr.getValue() + "]";
    }

    @Override
    public String visitVariable(Variable expr) {
        return "VAR[" + expr.getName() + "]";
    }

    @Override
    public String visitParenthesis(ParenthesisExpression expr) {
        var childValue = expr.getChild().accept(this);
        return "PAR(" + childValue + ")";
    }

    public static final DebugRepresentationExpressionVisitor INSTANCE =
            new DebugRepresentationExpressionVisitor();
}
