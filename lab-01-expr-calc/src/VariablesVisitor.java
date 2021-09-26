import java.util.HashMap;
import java.util.Scanner;

public class VariablesVisitor implements ExpressionVisitor {
    private final HashMap<String, Double> mVariablesValues;
    private final Scanner mScanner;

    VariablesVisitor(Scanner scanner) {
        mVariablesValues = new HashMap<>();
        mScanner = scanner;
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

    private void readVariable(String name) {
        System.out.print("value for '" + name + "': ");
        mVariablesValues.put(name, mScanner.nextDouble());
        System.out.println();
    }

    @Override
    public Object visitVariable(Variable expr) {
        var name = expr.getName();
        if (!mVariablesValues.containsKey(name)) {
            readVariable(name);
        }
        expr.setValue(mVariablesValues.get(name));
        return null;
    }

    @Override
    public Object visitParenthesis(ParenthesisExpression expr) {
        expr.getChild().accept(this);
        return null;
    }
}
