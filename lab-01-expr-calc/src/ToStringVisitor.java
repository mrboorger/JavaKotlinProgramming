public class ToStringVisitor implements ExpressionVisitor {
    private ToStringVisitor() {}

    @Override
    public String visitBinaryExpression(BinaryExpression expr) {
        var leftValue = (String)expr.getLeft().accept(this);
        var rightValue = (String)expr.getRight().accept(this);
        return leftValue + " " + expr.getOperation().mOpSymbol + " " + rightValue;
    }

    @Override
    public String visitLiteral(Literal expr) {
        return String.valueOf(expr.getValue());
    }

    @Override
    public String visitVariable(Variable expr) {
        return expr.getName();
    }

    @Override
    public String visitParenthesis(ParenthesisExpression expr) {
        return "(" + expr.getChild().accept(this) + ")";
    }

    public static final ToStringVisitor INSTANCE = new ToStringVisitor();
}
