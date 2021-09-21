public interface Variable extends Expression {
    @Override
    default Object accept(ExpressionVisitor visitor) {
        return visitor.visitVariable(this);
    }

    String getName();
    public double getValue();
    void setValue(double value);
}
