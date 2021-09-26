import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParserImpl implements Parser {
    enum Operation {
        ADDITION(3),
        SUBTRACTION(3),
        MULTIPLICATION(2),
        DIVISION(2),
        OPENING_PARANTHESIS(111),
        CLOSING_PARANTHESIS(111);

        private final int mPriority;  // lower = more priority

        Operation(int priority) {
            mPriority = priority;
        }

        static boolean isMorePriority(Operation lhs, Operation rhs) {
            return lhs.mPriority < rhs.mPriority;
        }

        static boolean isEqualPriority(Operation lhs, Operation rhs) {
            return lhs.mPriority == rhs.mPriority;
        }

        static boolean isNotLessPriority(Operation lhs, Operation rhs) {
            return lhs.mPriority <= rhs.mPriority;
        }

        private static Operation fromString(String str) {
            switch (str) {
                case "+": {
                    return ADDITION;
                }
                case "-": {
                    return SUBTRACTION;
                }
                case "*": {
                    return MULTIPLICATION;
                }
                case "/": {
                    return DIVISION;
                }
                case "(": {
                    return OPENING_PARANTHESIS;
                }
                case ")": {
                    return CLOSING_PARANTHESIS;
                }
                default: {
                    assert true : "Invalid operation " + str;
                    return ADDITION;
                }
            }
        }
    }

    static private void processOperations(Stack<Operation> operations,
                                          Stack<Expression> expressions,
                                          String input) throws ExpressionParseException {
        if (expressions.size() < 2) {
            throwExpressionParseException(input);
        }
        if (operations.peek() == Operation.OPENING_PARANTHESIS) {
            throwExpressionParseException(input);
        }
        var newBinExpr = BinaryExpression.OpType.valueOf(operations.pop().toString());
        var secondOperand = expressions.pop();
        var firstOperand = expressions.pop();
        expressions.push(new BinaryExpressionImpl(newBinExpr, firstOperand, secondOperand));
    }

    private List<String> splitToTokens(String input) {
        var tokens = new ArrayList<String>();
        for (var str : input.split("\\s+")) {
            // (?<=[+\-*/()])|(?=[+\-*/()])
            for (var str2 : str.split("(?<=[()])|(?=[()])")) {
                if (!str2.isEmpty()) {
                    tokens.add(str2);
                }
            }
        }
        return tokens;
    }

    public Expression parseExpression(String input) throws ExpressionParseException {
        var operations = new Stack<Operation>();
        var expressions = new Stack<Expression>();
        boolean isFirstOp = true;
        for (var strTok : splitToTokens(input)) {
            switch (strTok) {
                case "-":
                case "+":
                case "*":
                case "/": {
                    // unary operation
                    if (isFirstOp) {
                        throwExpressionParseException(input);
                    }
                    var newOperation = Operation.fromString(strTok);
                    while (!operations.empty() &&
                            operations.peek() != Operation.OPENING_PARANTHESIS &&
                            Operation.isNotLessPriority(operations.peek(), newOperation)) {
                        processOperations(operations, expressions, input);
                    }
                    operations.push(newOperation);
                    break;
                }
                case "(": {
                    operations.push(Operation.OPENING_PARANTHESIS);
                    break;
                }
                case ")": {
                    while (!operations.empty() && operations.peek() != Operation.OPENING_PARANTHESIS) {
                        processOperations(operations, expressions, input);
                    }
                    if (operations.empty() || operations.pop() != Operation.OPENING_PARANTHESIS ||
                        expressions.empty()) {
                        throwExpressionParseException(input);
                    }
                    expressions.push(new ParanthesisExpressionImpl(expressions.pop()));
                    break;
                }
                default: {
                    // variable or constant
                    expressions.push(CreateVariableOrConstant(strTok));
                    break;
                }
            }
            isFirstOp = false;
        }

        while(!operations.empty()) {
            processOperations(operations, expressions, input);
        }

        if (!operations.empty() || expressions.size() != 1) {
            throwExpressionParseException(input);
        }
        return expressions.pop();
    }

    static private void throwExpressionParseException(String input) throws ExpressionParseException {
        throw new ExpressionParseException("Invalid expression " + input);
    }

    
    static boolean isVariable(String str) {
        assert str != null : "";
        return (!str.isEmpty() && Character.isLetter(str.charAt(0)));
    }

    static private Expression CreateVariableOrConstant(String str) throws ExpressionParseException {
        Double value = convertToDouble(str);
        if (value != null) {
            return new LiteralImpl(value);
        }
        if (isVariable(str)) {
            return new VariableImpl(str);
        }
        throwExpressionParseException(str);
        return null;
    }

    static private Double convertToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
