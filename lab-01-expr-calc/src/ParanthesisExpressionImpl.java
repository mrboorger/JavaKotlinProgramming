public class ParanthesisExpressionImpl implements ParenthesisExpression {
    private final Expression mChild;

    public ParanthesisExpressionImpl(Expression child) {
        mChild = child;
    }

    @Override
    public Expression getChild() {
        return mChild;
    }

    @Override
    public Object accept(ExpressionVisitor visitor) {
        return visitor.visitParenthesis(this);
    }
}
