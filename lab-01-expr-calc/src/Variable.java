public class Variable implements Literal {
    private String mName;

    Variable(String name) {
        mName = name;
    }

    @Override
    public double getValue() {
        return 0;
    }
}
