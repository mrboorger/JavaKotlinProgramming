public interface Variable extends Expression {
    default Object accept(ExpressionVisitor visitor) {
        return visitor.visitVariable(this);
    }
    String getName();
    double getValue();
}
