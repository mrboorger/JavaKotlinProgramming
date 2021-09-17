import java.util.HashMap;
import java.util.HashSet;

public class VariablesManagerImpl implements VariablesManager {
    private HashMap<String, Double> mValues;
    private HashSet<String> mNames;

    private VariablesManagerImpl() {
        mValues = new HashMap<String, Double>();
        mNames = new HashSet<String>();
    }

    public void readValues() {
        // TODO: do it
        assert false : "To be written";
    }

    public void set() {
        // TODO: do it
        assert false : "To be written";
    }

    @Override
    public double getValueFromName(String name) {
        assert mValues.containsKey(name) : "VariablesManager doesn't contain name " + name;
        return mValues.get(name);
    }
}
