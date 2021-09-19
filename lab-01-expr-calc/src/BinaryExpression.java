public interface BinaryExpression extends Expression {
    enum OpType {
        ADDITION,
        SUBTRACTION;
    }

    Expression getLeft();
    Expression getRight();
    OpType getOperation();  // return type looks like enum, right?
}
