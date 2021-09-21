public class VariableImpl implements Variable {
    private final String mName;
    private double mValue;

    VariableImpl(String name) {
        mName = name;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public double getValue() {
        return mValue;
    }
}
