public interface Variable extends Expression {
    @Override
    default <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }

    String getName();
    double getValue();
    void setValue(double value);
}
