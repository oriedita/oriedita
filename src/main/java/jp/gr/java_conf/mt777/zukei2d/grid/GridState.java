package jp.gr.java_conf.mt777.zukei2d.grid;

/**
 * The state of the grid, either within the paper, spanning the whole screen or hidden.
 */
public enum GridState {
    HIDDEN(0),
    WITHIN_PAPER(1),
    FULL(2);

    int state;

    GridState(int state) {
        this.state = state;
    }

    public GridState advance() {
        try {
            return from(state + 1);
        } catch (IllegalArgumentException e) {
            return HIDDEN;
        }
    }

    public static GridState from(String state) {
        return from(Integer.parseInt(state));
    }

    public static GridState from(int state) {
        for (var val : values()) {
            if (val.getState() == state) {
                return val;
            }
        }

        throw new IllegalArgumentException();
    }

    public int getState() {
        return state;
    }

    @Override
    public String toString() {
        return Integer.toString(state);
    }
}
