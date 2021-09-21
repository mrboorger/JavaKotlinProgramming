import java.util.Stack;

public class ParserImpl implements Parser {
    enum Operation {
        ADDITION,
        SUBTRACTION;

        private static Operation fromString(String str) {
            switch (str) {
                case "+": {
                    return ADDITION;
                }
                case "-": {
                    return SUBTRACTION;
                }
                default: {
                    assert true : "Invalid operation " + str;
                    return ADDITION;
                }
            }
        }
    }

    public Expression parseExpression(String input) throws ExpressionParseException {
        var operations = new Stack<Operation>();
        var expressions = new Stack<Expression>();
        boolean isPrevVariableOrConstant = false;
        for (var strTok : input.split("\\s+")) {
            switch (strTok) {
                case "-":
                case "+": {
                    // unary operation
                    if (!isPrevVariableOrConstant) {
                        throwExpressionParseException(input);
                    }
                    isPrevVariableOrConstant = false;
                    var newOperation = Operation.fromString(strTok);
                    while (!operations.empty()) {
                        if (expressions.size() < 2) {
                            throwExpressionParseException(input);
                        }
                        var newBinExpr = BinaryExpression.OpType.valueOf(operations.pop().toString());
                        // TODO: remove double of code
                        var secondOperand = expressions.pop();
                        var firstOperand = expressions.pop();
                        expressions.push(new BinaryExpressionImpl(newBinExpr, firstOperand, secondOperand));
                    }
                    operations.push(newOperation);
                    break;
                }
                default: {
                    // variable or constant
                    isPrevVariableOrConstant = true;
                    expressions.push(CreateVariableOrConstant(strTok));
                    break;
                }
            }
        }

        while(!operations.empty()) {
            var newOperation = operations.pop();
            if (expressions.size() < 2) {
                throwExpressionParseException(input);
            }
            var newBinExpr = BinaryExpression.OpType.valueOf(newOperation.toString());
            var secondOperand = expressions.pop();
            var firstOperand = expressions.pop();
            expressions.push(new BinaryExpressionImpl(newBinExpr, firstOperand, secondOperand));
        }


        if (!operations.empty() || expressions.size() != 1) {
            throwExpressionParseException(input);
        }
        return expressions.pop();
    }

    static private void throwExpressionParseException(String input) throws ExpressionParseException {
        throw new ExpressionParseException("Invalid expression " + input);
    }

    // TODO: move to Literal interface
    static private Expression CreateVariableOrConstant(String str) {
        Double value = convertToDouble(str);
        // TODO: check is literal
        return (value == null ? new VariableImpl(str) : new LiteralImpl(value));
    }

    static private Double convertToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
