public interface Literal extends Expression {
    @Override
    default <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
    double getValue();
}
