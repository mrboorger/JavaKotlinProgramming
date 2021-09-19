public class BinaryExpressionImpl implements BinaryExpression {
    private OpType mOperationType;
    private Expression mLeftChild;
    private Expression mRightChild;

    public BinaryExpressionImpl(OpType operationType, Expression left, Expression right) {
        mOperationType = operationType;
        mLeftChild = left;
        mRightChild = right;
    }

    public void setmLeftChild(Expression leftChild) {
        this.mLeftChild = leftChild;
    }

    public void setmRightChild(Expression rightChild) {
        this.mRightChild = rightChild;
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
    public Object accept(ExpressionVisitor visitor) {
        return visitor.visitBinaryExpression(this);
    }
}
