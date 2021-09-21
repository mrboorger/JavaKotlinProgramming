public class CalcDepthVisitor implements ExpressionVisitor {
    @Override
    public Integer visitBinaryExpression(BinaryExpression expr) {
        var leftValue = (Integer)expr.getLeft().accept(this);
        var rightValue = (Integer)expr.getRight().accept(this);
        return Math.max(leftValue, rightValue) + 1;
    }

    @Override
    public Integer visitLiteral(Literal expr) {
        return 1;
    }

    @Override
    public Integer visitVariable(Variable expr) {
        return 1;
    }
}
