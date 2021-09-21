public class DebugRepresentationExpressionVisitor implements ExpressionVisitor {
    @Override
    public String visitBinaryExpression(BinaryExpression expr) {
        var leftValue = (String)expr.getLeft().accept(this);
        var RightValue = (String)expr.getRight().accept(this);
        return expr.getOperation().getOpName() + "(" + leftValue + ", " + RightValue + ")";
    }

    @Override
    public String visitLiteral(Literal expr) {
        return expr.getRepresentation();
    }
}
