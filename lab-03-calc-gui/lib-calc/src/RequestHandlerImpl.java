import java.util.Locale;
import java.util.Scanner;

public class RequestHandlerImpl implements RequestHandler {
    @Override
    public void request() {
        var in = new Scanner(System.in).useLocale(Locale.US);
        var parser = new ParserImpl();
        Expression root = null;
        while (root == null) {
            System.out.print("enter expression: ");
            var strExpr = in.nextLine();
            System.out.println();
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                System.out.println("Invalid expression. Enter valid one");
            }
        }
        var strDebug = root.accept(DebugRepresentationExpressionVisitor.INSTANCE);
        System.out.println("tree: " + strDebug);
        var str = root.accept(ToStringVisitor.INSTANCE);
        System.out.println("to string: " + str);
        var depth = root.accept(CalcDepthVisitor.INSTANCE);
        System.out.println("expr-tree depth: " + depth.toString());
        root.accept(new VariablesVisitor(in));
        var result = (double)root.accept(ComputeExpressionVisitor.INSTANCE);
        System.out.println("result: " + result);
    }
}
