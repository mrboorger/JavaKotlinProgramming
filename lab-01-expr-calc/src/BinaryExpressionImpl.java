public class BinaryExpressionImpl implements BinaryExpression {
    private OpType mOperationType;
    private Expression mLeftChild;
    private Expression mRightChild;

    @Override
    public Expression getLeft() {
        return mLeftChild;
    }

    @Override
    public Expression getRight() {
        return mRightChild;
    }

    @Override
    public OpType getOperation() {
        return mOperationType;
    }

    @Override
    public Object accept(ExpressionVisitor visitor) {
        return visitor.visitBinaryExpression(this);
    }
}
