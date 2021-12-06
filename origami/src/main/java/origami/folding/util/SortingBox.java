package origami.folding.util;

import java.util.ArrayList;

public class SortingBox<T> {//Arrange and store data in ascending order of double
    private final ArrayList<WeightedValue<T>> i_d_List = new ArrayList<>();

    public SortingBox() {
        i_d_List.add(new WeightedValue<>(null, -1.0));
    }

    public void reset() {
        i_d_List.clear();
        i_d_List.add(new WeightedValue<>());
    }

    public int getTotal() {
        return i_d_List.size() - 1;
    }

    public WeightedValue<T> getWeightedValue(int i) {//Extract the value-th int_double from the front in the sorting box
        WeightedValue<T> i_d_temp = new WeightedValue<>();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp;
    }

    public int getSequence(T i_of_i_d) {//As a result of being arranged, the order of int is returned. If int has the same value, the result will be strange
        for (int i = 1; i <= getTotal(); i++) {
            if (i_of_i_d == getValue(i)) {
                return i;
            }
        }

        return 0;//Since the order cannot be 0, 0 is returned if the int order cannot be found.
    }

    public T getValue(int i) {//Returns an int that is the value-th order as a result of being arranged
        return i_d_List.get(i).getValue();
    }

    public T backwardsGetValue(int iu) {//Returns the value-th int from the back
        int i = getTotal() + 1 - iu;
        return i_d_List.get(i).getValue();
    }

    public double getWeight(int i) {//As a result of arranging, returns a double paired with an int that is in the value-th order.
        return i_d_List.get(i).getWeight();
    }

    public void add(WeightedValue<T> i_d_0) {//Simply add int_double to the end
        i_d_List.add(i_d_0);
    }

    public void addByWeight(T value, double weight) {
        WeightedValue<T> i_d_0 = new WeightedValue<>(value, weight);
        for (int i = 1; i <= getTotal(); i++) {
            if (i_d_0.getWeight() < getWeight(i)) {
                i_d_List.add(i, i_d_0);
                return;
            }
        }

        i_d_List.add(i_d_0);
    }

    public void set(SortingBox<T> nbox) {
        reset();
        for (int i = 1; i <= nbox.getTotal(); i++) {
            i_d_List.add(nbox.getWeightedValue(i));
        }
    }

    // Move the second element to the first, third to the second, etc. Move the first element to the end of the list.
    public void shift() {
        SortingBox<T> nbox = new SortingBox<>();
        for (int i = 2; i <= getTotal(); i++) {
            nbox.add(getWeightedValue(i));
        }
        nbox.add(getWeightedValue(1));

        set(nbox);
    }
}



