public class Variable implements Literal {
    private final String mName;

    Variable(String name) {
        mName = name;
    }

    @Override
    public String getRepresentation() {
        return "VAR[" + mName + "]";
    }

    @Override
    public double getValue() {
        return 0;
    }
}
