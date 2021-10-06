package origami_editor.sortingbox;

import java.util.ArrayList;

public class SortingBox<T> {//Arrange and store data in ascending order of double
    private final ArrayList<WeightedValue<T>> i_d_List = new ArrayList<>();

    public SortingBox() {
    }

    public void reset() {
        i_d_List.clear();
    }

    public Iterable<WeightedValue<T>> values() {
        return i_d_List;
    }

    public int getTotal() {
        return i_d_List.size();
    }

    public WeightedValue<T> getWeightedValue(int i) {//Extract the value-th int_double from the front in the sorting box
        WeightedValue<T> i_d_temp = new WeightedValue<>();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp;
    }

    public int getSequence(T i_of_i_d) {//As a result of being arranged, the order of int is returned. If int has the same value, the result will be strange
        for (int i = 0; i < i_d_List.size(); i++) {
            if (i_of_i_d == i_d_List.get(i)) {
                return i;
            }
        }

        return 0;//Since the order cannot be 0, 0 is returned if the int order cannot be found.
    }

    public T getValue(int i) {//Returns an int that is the value-th order as a result of being arranged
        WeightedValue<T> i_d_temp = new WeightedValue<>();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getValue();
    }

    public T backwardsGetValue(int iu) {//Returns the value-th int from the back
        int i = i_d_List.size() - 1 - iu;
        WeightedValue<T> i_d_temp = new WeightedValue<>();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getValue();
    }

    public double getWeight(int i) {//As a result of arranging, returns a double paired with an int that is in the value-th order.
        WeightedValue<T> i_d_temp = new WeightedValue<>();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getWeight();
    }

    public void add(WeightedValue<T> i_d_0) {//Simply add int_double to the end
        i_d_List.add(i_d_0);
    }

    public void container_i_smallest_first(WeightedValue<T> i_d_0) {//The meaning of the name of this function is to put value in ascending order of weight, but it may be confusing.
        for (int i = 0; i < i_d_List.size(); i++) {
            if (i_d_0.getWeight() < i_d_List.get(i).getWeight()) {
                i_d_List.add(i, i_d_0);
                return;
            }
        }

        i_d_List.add(i_d_0);
    }

    public void set(SortingBox<T> nbox) {
        reset();
        for (WeightedValue<T> wv : nbox.values()) {
            i_d_List.add(wv);
        }
    }

    // Move the second element to the first, third to the second, etc. Move the first element to the end of the list.
    public void shift() {
        SortingBox<T> nbox = new SortingBox<>();
        for (int i = 1; i < i_d_List.size(); i++) {
            nbox.add(i_d_List.get(i));
        }
        nbox.add(i_d_List.get(0));

        set(nbox);
    }

    public void display() {
        System.out.println("--- narabebako.hyouji() ---");
        for (int k = 0; k < i_d_List.size(); k++) {
            WeightedValue<T> i_d_temp = new WeightedValue<>();
            i_d_temp.set(i_d_List.get(k));

            System.out.println("   Narabebako.hyouj " + k + " : " + i_d_temp.getValue() + "," + i_d_temp.getWeight());
        }
    }
}



