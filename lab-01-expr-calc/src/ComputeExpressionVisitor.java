public class ComputeExpressionVisitor implements ExpressionVisitor {
    public Double visitBinaryExpression(BinaryExpression expr) {
        var leftValue = (Double)expr.getLeft().accept(this);
        var RightValue = (Double)expr.getRight().accept(this);
        switch (expr.getOperation()) {
            case ADDITION: {
                return leftValue + RightValue;
            }
            case SUBTRACTION: {
                return leftValue - RightValue;
            }
//            case default: {
//            }
        }
        assert false: "Unreachable";
        return 0.0;
    }
    public Double visitLiteral(Literal expr) {
        return expr.getValue();
    }
    public Double visitParenthesis(ParenthesisExpression expr) {
        return 1.0;
    }
}