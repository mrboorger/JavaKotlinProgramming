public class Constant implements Literal {
    private double mValue;

    Constant(double value) {
        mValue = value;
    }

    @Override
    public double getValue() {
        return mValue;
    }
}
