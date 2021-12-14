public class LiteralImpl implements Literal {
    private final double mValue;

    LiteralImpl(double value) {
        mValue = value;
    }

    @Override
    public double getValue() {
        return mValue;
    }
}
