package origami_editor.sortingbox;

public class WeightedValue<T> {
    T value = null;
    double weight = 0.0;

    public WeightedValue() {
    }

    public WeightedValue(T i0, double d0) {
        value = i0;
        weight = d0;
    }

    public WeightedValue(WeightedValue<T> other) {
        value = other.getValue();
        weight = other.getWeight();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T i0) {
        value = i0;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double d0) {
        weight = d0;
    }

    public void set(WeightedValue<T> i_d_0) {
        value = i_d_0.getValue();
        weight = i_d_0.getWeight();
    }
}
