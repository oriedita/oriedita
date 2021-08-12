package jp.gr.java_conf.mt777.seiretu.narabebako;

public class int_double {
    int i = 0;
    double d = 0.0;

    public int_double() {
    }

    public int_double(int i0, double d0) {
        i = i0;
        d = d0;
    }

    public void setInteger(int i0) {
        i = i0;
    }

    public void setDouble(double d0) {
        d = d0;
    }

    public int getInteger() {
        return i;
    }

    public double getDouble() {
        return d;
    }

    public void set(int_double i_d_0) {
        i = i_d_0.getInteger();
        d = i_d_0.getDouble();
    }
}
