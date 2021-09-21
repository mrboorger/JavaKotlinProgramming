public class Constant implements Literal {
    private double mValue;

    Constant(double value) {
        mValue = value;
    }

    @Override
    public String getRepresentation() {
        return "CON[" + String.valueOf(mValue) + "]";
    }

    @Override
    public double getValue() {
        return mValue;
    }
}
