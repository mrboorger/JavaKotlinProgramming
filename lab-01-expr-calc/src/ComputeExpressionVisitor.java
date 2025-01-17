public class ComputeExpressionVisitor implements ExpressionVisitor<Double> {
    private ComputeExpressionVisitor() {}

    @Override
    public Double visitBinaryExpression(BinaryExpression expr) {
        var leftValue = expr.getLeft().accept(this);
        var rightValue = expr.getRight().accept(this);
        switch (expr.getOperation()) {
            case ADDITION : {
                return leftValue + rightValue;
            }
            case SUBTRACTION : {
                return leftValue - rightValue;
            }
            case MULTIPLICATION : {
                return leftValue * rightValue;
            }
            case DIVISION : {
                return leftValue / rightValue;
            }
        }
        assert false: "Unreachable";
        return 0.0;
    }

    @Override
    public Double visitLiteral(Literal literal) {
        return literal.getValue();
    }

    @Override
    public Double visitVariable(Variable variable) {
        return variable.getValue();
    }

    @Override
    public Double visitParenthesis(ParenthesisExpression expr) {
        return expr.getChild().accept(this);
    }

    public static final ComputeExpressionVisitor INSTANCE = new ComputeExpressionVisitor();
}