public class ParenthesisExpressionImpl implements ParenthesisExpression {
    private final Expression mChild;

    public ParenthesisExpressionImpl(Expression child) {
        mChild = child;
    }

    @Override
    public Expression getChild() {
        return mChild;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitParenthesis(this);
    }
}
