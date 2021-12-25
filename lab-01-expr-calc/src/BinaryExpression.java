public interface BinaryExpression extends Expression {
    enum OpType {
        ADDITION('+', "ADD"),
        SUBTRACTION('-', "SUB"),
        MULTIPLICATION('*', "MUL"),
        DIVISION('/', "DIV");


        final char mOpSymbol;
        final String mOpName;

        OpType(char opSymbol, String opName) {
            mOpSymbol = opSymbol;
            mOpName = opName;
        }

        public char getOpSymbol() {
            return mOpSymbol;
        }
        public String getOpName() {
            return mOpName;
        }
    }

    Expression getLeft();
    Expression getRight();
    OpType getOperation();  // return type looks like enum, right?
}
