public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("enabletests")) {
            new TesterImpl().runAllTests();
        }
        new RequestHandlerImpl().request();
    }
}
