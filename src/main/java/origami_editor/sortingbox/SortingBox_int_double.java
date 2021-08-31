package origami_editor.sortingbox;

import java.util.ArrayList;

public class SortingBox_int_double {//Arrange and store data in ascending order of double
    private final ArrayList<int_double> i_d_List = new ArrayList<>();

    public SortingBox_int_double() {
        i_d_List.add(new int_double(0, -1.0));
    }

    public void reset() {
        i_d_List.clear();
        i_d_List.add(new int_double());
    }

    public int getTotal() {
        return i_d_List.size() - 1;
    }

    public int_double get_i_d(int i) {//Extract the i-th int_double from the front in the sorting box
        int_double i_d_temp = new int_double();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp;
    }

    public int getSequence(int i_of_i_d) {//As a result of being arranged, the order of int is returned. If int has the same value, the result will be strange
        for (int i = 1; i <= getTotal(); i++) {
            if (i_of_i_d == getInt(i)) {
                return i;
            }
        }

        return 0;//Since the order cannot be 0, 0 is returned if the int order cannot be found.
    }

    public int getInt(int i) {//Returns an int that is the i-th order as a result of being arranged
        int_double i_d_temp = new int_double();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getInteger();
    }

    public int backwards_get_int(int iu) {//Returns the i-th int from the back
        int i = getTotal() + 1 - iu;
        int_double i_d_temp = new int_double();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getInteger();
    }

    public double getDouble(int i) {//As a result of arranging, returns a double paired with an int that is in the i-th order.
        int_double i_d_temp = new int_double();
        i_d_temp.set(i_d_List.get(i));
        return i_d_temp.getDouble();
    }

    public void add(int_double i_d_0) {//Simply add int_double to the end
        i_d_List.add(i_d_0);
    }

    public void add(int i, int_double i_d_0) {//int_doubleを単にi番目にに加える（挿入する）
        i_d_List.add(i, i_d_0);
    }

    public void container_i_smallest_first(int_double i_d_0) {//The meaning of the name of this function is to put i in ascending order of d, but it may be confusing.
        for (int i = 1; i <= getTotal(); i++) {
            if (i_d_0.getDouble() < getDouble(i)) {
                i_d_List.add(i, i_d_0);
                return;
            }
        }

        i_d_List.add(i_d_0);
    }

    public void set(SortingBox_int_double nbox) {
        reset();
        for (int i = 1; i <= nbox.getTotal(); i++) {
            i_d_List.add(nbox.get_i_d(i));
        }
    }

    // Move the second element to the first, third to the second, etc. Move the first element to the end of the list.
    public void shift() {
        SortingBox_int_double nbox = new SortingBox_int_double();
        for (int i = 2; i <= getTotal(); i++) {
            nbox.add(get_i_d(i));
        }
        nbox.add(get_i_d(1));

        set(nbox);
    }

    public void display() {
        System.out.println("--- narabebako.hyouji() ---");
        for (int k = 1; k <= getTotal(); k++) {
            int_double i_d_temp = new int_double();
            i_d_temp.set(i_d_List.get(k));

            System.out.println("   Narabebako.hyouj " + k + " : " + i_d_temp.getInteger() + "," + i_d_temp.getDouble());
        }
    }
}



