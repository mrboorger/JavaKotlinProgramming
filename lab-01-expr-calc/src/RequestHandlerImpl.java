import java.util.Scanner;

public class RequestHandlerImpl implements RequestHandler {
    @Override
    public void request() {
        var in = new Scanner(System.in);
        var parser = new ParserImpl();
        Expression root = null;
        while (root == null) {
            System.out.print("enter expression: ");
            var strExpr = in.nextLine();
            try {
                root = parser.parseExpression(strExpr);
            } catch (ExpressionParseException e) {
                System.out.println("Invalid expression. Enter valid one");
            }
        }
        var strDebug = root.accept(new DebugRepresentationExpressionVisitor());
        System.out.println("tree: " + strDebug);
        var depth = root.accept(new CalcDepthVisitor());
        System.out.println("expr-tree depth: " + depth.toString());
    }
}
