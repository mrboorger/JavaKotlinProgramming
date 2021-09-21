public interface BinaryExpression extends Expression {
    enum OpType {
        ADDITION('+', "ADD"),
        SUBTRACTION('-', "SUB");

        char mOpSymbol;
        String mOpName;

        OpType(char opSymbol, String opName) {
            mOpSymbol = opSymbol;
            mOpName = opName;
        }

        public char GetOpSymbol() {
            return mOpSymbol;
        }
        public String GetOpName() {
            return mOpName;
        }
    }

    Expression getLeft();
    Expression getRight();
    OpType getOperation();  // return type looks like enum, right?
}
