import java.util.Stack;

public class ParserImpl implements Parser {
    public Expression parseExpression(String input) throws ExpressionParseException {
        // divide input to tokens
        // TODO: rewrite it
        var operations = new Stack<String>();
        var literals = new Stack<String>();
        for (var strTok : input.split("\\s+")) {
            if (strTok.equals("-") || strTok.equals("+")) {
                operations.push(strTok);
            } else {
                literals.push(strTok);
            }
        }

        Expression root = null;
        while (operations.size() > 0) {
            var strOp = operations.pop();
            var opType = BinaryExpression.OpType.ADDITION;
            if (strOp.equals("-")) {
                opType = BinaryExpression.OpType.SUBTRACTION;
            }

            if ((root == null && literals.size() < 2) || (root != null && literals.size() < 1)) {
                throw new ExpressionParseException("Invalid expression " + input);
            }

            if (root == null) {
                root = new BinaryExpressionImpl(opType, CreateLiteral(literals.pop()), CreateLiteral(literals.pop()));
            } else {
                root = new BinaryExpressionImpl(opType, root, CreateLiteral(literals.pop()));
            }
        }
        if (literals.size() > 0 && root == null) {
            root = CreateLiteral(literals.pop());
        }
        if (literals.size() > 0) {
            throw new ExpressionParseException("Invalid expression " + input);
        }

        return null;
    }

    static private Literal CreateLiteral(String strLiteral) {
        Double value = ConvertToDouble(strLiteral);
        return (value == null ? new Variable(strLiteral) : new Constant(value));
    }

    static private Double ConvertToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
