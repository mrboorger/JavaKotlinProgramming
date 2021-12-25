public class BinaryExpressionImpl implements BinaryExpression {
    private final OpType mOperationType;
    private final Expression mLeftChild;
    private final Expression mRightChild;

    public BinaryExpressionImpl(OpType operationType, Expression left, Expression right) {
        mOperationType = operationType;
        mLeftChild = left;
        mRightChild = right;
    }

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
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
}
