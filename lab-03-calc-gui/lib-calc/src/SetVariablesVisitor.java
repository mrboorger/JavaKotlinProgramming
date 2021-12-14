import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class SetVariablesVisitor implements ExpressionVisitor {
    private final HashMap<String, Double> mVariablesNames;

    public SetVariablesVisitor(HashMap<String, Double> variablesNames) {
        mVariablesNames = variablesNames;
    }

    private Double getVariable(String name) throws ExpressionParseException {
        if (!mVariablesNames.containsKey(name)) {
            throw new ExpressionParseException("Variable " + name + "is missing");
        }
        return mVariablesNames.get(name);
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression expr) {
        expr.getLeft().accept(this);
        expr.getRight().accept(this);
        return null;
    }

    @Override
    public Object visitLiteral(Literal expr) {
        return null;
    }

    @Override
    public Object visitVariable(Variable expr) {
        var name = expr.getName();
        try {
            expr.setValue(getVariable(name));
        } catch (ExpressionParseException e) {

        }
        return null;
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        expr.getChild().accept(this);
        return null;
    }
}
