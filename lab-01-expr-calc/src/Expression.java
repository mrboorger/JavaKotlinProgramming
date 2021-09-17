public interface Expression {
    Object accept(ExpressionVisitor visitor);
}
