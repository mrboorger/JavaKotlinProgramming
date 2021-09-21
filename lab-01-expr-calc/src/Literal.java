public interface Literal extends Expression {
    @Override
    default Object accept(ExpressionVisitor visitor) {
        return visitor.visitLiteral(this);
    }
    String getRepresentation();
    double getValue();
}