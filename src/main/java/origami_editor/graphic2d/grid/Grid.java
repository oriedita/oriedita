package origami_editor.graphic2d.grid;

import origami_editor.graphic2d.linesegment.LineSegment;
import origami_editor.graphic2d.oritacalc.OritaCalc;
import origami_editor.graphic2d.point.Point;
import origami_editor.tools.camera.Camera;

import java.awt.*;

// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------
public class Grid {
    double d_grid_width = 200.0;//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<格子幅(double)

    double d_grid_a_length = 1.0;
    double d_grid_b_length = 1.0;
    double gridAngle = -90.0;

    double d_grid_ax = 1.0;//格子の横方向の単位ベクトルのX成分の比率
    double d_grid_ay = 0.0;//格子の横方向の単位ベクトルのY成分の比率

    double d_grid_bx = 0.0;//格子の縦方向の単位ベクトルのX成分の比率
    double d_grid_by = 1.0;//格子の縦方向の単位ベクトルのY成分の比率

    double okx0 = -200.0;//obiject系での格子のx座標の原点
    double oky0 = +200.0;//obiject系での格子のy座標の原点

    double diagonal_max = 1.0;//The longer diagonal of the unit cell
    double diagonal_min = 1.0;//The shorter diagonal of the unit cell

    State baseState = State.WITHIN_PAPER;//Base (grid) status = 0 is invalid for the whole area, but only the grid width is valid since it is used to set the radius of attraction to the existing endpoint, status = 1 is valid only within the paper, and status = 2 is valid for the whole area.

    //用紙の分割なしならgrid_zahyou[0から1]なのでgrid_bunkatu_suuは１、gridSize
    //用紙の2分割ならgrid_zahyou[0,1,2]なのでgrid_bunkatu_suuは2、
    //用紙の4分割ならgrid_zahyou[0,1,2,3,4]なのでgrid_bunkatu_suuは4、
    int gridSize = 2;

    int a_to_parallel_scale_interval = 5;
    int a_to_parallel_scale_position = 0;


    int b_to_parallel_scale_interval = 5;
    int b_to_parallel_scale_position = 0;

    Color grid_color = new Color(230, 230, 230);//格子線の色
    Color gridScaleColor = new Color(180, 200, 180);//格子目盛り線の色

    int gridLineWidth = 1;//Grid line width

    public Grid() {

    }

    // ------------------------------------------------------
    public void set_a_to_parallel_scale_interval(int i) {
        a_to_parallel_scale_interval = i;
        if (a_to_parallel_scale_position >= a_to_parallel_scale_interval) {
            a_to_parallel_scale_position = 0;
        }
    }

    public void set_b_to_parallel_scale_interval(int i) {
        b_to_parallel_scale_interval = i;
        if (b_to_parallel_scale_position >= b_to_parallel_scale_interval) {
            b_to_parallel_scale_position = 0;
        }
    }

    public void a_to_parallel_scale_position_change() {
        a_to_parallel_scale_position = a_to_parallel_scale_position + 1;
        if (a_to_parallel_scale_position >= a_to_parallel_scale_interval) {
            a_to_parallel_scale_position = 0;
        }
    }

    public void b_to_parallel_scale_position_change() {
        b_to_parallel_scale_position = b_to_parallel_scale_position + 1;
        if (b_to_parallel_scale_position >= b_to_parallel_scale_interval) {
            b_to_parallel_scale_position = 0;
        }
    }

    public void setGridLineWidth(int i0) {
        gridLineWidth = i0;
    }

    public int get_a_to_parallel_scale_position() {
        return a_to_parallel_scale_position;
    }

    public void set_a_to_parallel_scale_position(int i0) {
        a_to_parallel_scale_position = i0;
    }

    public int get_b_to_parallel_scale_position() {
        return b_to_parallel_scale_position;
    }

    public void set_b_to_parallel_scale_position(int i0) {
        b_to_parallel_scale_position = i0;
    }

    public int getGridLIneWidth() {
        return gridLineWidth;
    }


    // ------------------------------------------------------
    public void setGridSize(int i) {
        gridSize = i;
        d_grid_width = 400.0 / (double) gridSize;

        calculateGrid();
    }

    // ------------------------------------------------------
    public double getGridWidth() {
        return d_grid_width;
    }

    public int divisionNumber() {
        return gridSize;
    }

    // ----------------------------------------
    public void setGrid(double dkxn, double dkyn, double dkk) {
        d_grid_a_length = dkxn;
        d_grid_b_length = dkyn;
        gridAngle = -dkk;

        calculateGrid();
    }

    // ----------------------------------------
    public void calculateGrid() {
        d_grid_ax = d_grid_width * d_grid_a_length;
        d_grid_ay = d_grid_width * 0.0;

        double d_rad = (Math.PI / 180) * gridAngle;
        d_grid_bx = d_grid_width * d_grid_b_length * Math.cos(d_rad);
        d_grid_by = d_grid_width * d_grid_b_length * Math.sin(d_rad);

        diagonal_max = OritaCalc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
        diagonal_min = OritaCalc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        if (diagonal_max < diagonal_min) {
            diagonal_min = OritaCalc.distance(new Point(0.0, 0.0), new Point(d_grid_ax + d_grid_bx, d_grid_ay + d_grid_by));
            diagonal_max = OritaCalc.distance(new Point(d_grid_ax, d_grid_ay), new Point(d_grid_bx, d_grid_by));
        }

        resetGrid();
    }

    private void resetGrid() {
        if (baseState == State.WITHIN_PAPER) {
            if (Math.abs(d_grid_a_length - 1.0) > 0.000001) {
                setBaseState(State.FULL);
            }
            if (Math.abs(d_grid_b_length - 1.0) > 0.000001) {
                setBaseState(State.FULL);
            }
            if (Math.abs(gridAngle - (-90.0)) > 0.000001) {
                setBaseState(State.FULL);
            }
        }
    }

    public void decreaseGridLineWidth() {
        gridLineWidth = gridLineWidth - 2;
        if (gridLineWidth < 1) {
            gridLineWidth = 1;
        }
    }

    public void increaseGridLineWidth() {
        gridLineWidth = gridLineWidth + 2;
    }

    public State getBaseState() {
        return baseState;
    }

    public void setBaseState(State i) {
        baseState = i;

        resetGrid();
    }

    public Point getIndex(Point t0) {//obj系座標のTenから、格子の指数を得る
        //行列 [d_grid_ax, d_grid_bx]によって[1]は格子ベクトルaに変換され、[1]は格子ベクトルbに変換される。
        //     [d_grid_ay, d_grid_by]        [0]　　　　　　　 　　　　　　[0]
        //この逆行列によってobj系座標のTenは格子の指数に変換される。
        //　　　　　
        //行列の記号の定義
        double ax = d_grid_ax;
        double ay = d_grid_ay;
        double bx = d_grid_bx;
        double by = d_grid_by;

        //逆行列の記号の定義
        double det = ax * by - bx * ay;
        double gax = by / det;
        double gay = -ay / det;
        double gbx = -bx / det;
        double gby = ax / det;


        double kx = t0.getX() - okx0;
        double ky = t0.getY() - oky0;

        double index_x = gax * kx + gbx * ky;
        double index_y = gay * kx + gby * ky;

        return new Point(index_x, index_y);
    }

    public int get_a_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より小さい整数の指数を得る。
        Point p_a_index = new Point(getIndex(p_a));
        Point p_b_index = new Point(getIndex(p_b));
        Point p_c_index = new Point(getIndex(p_c));
        Point p_d_index = new Point(getIndex(p_d));

        double a_index_min = p_a_index.getX();
        if (p_b_index.getX() < a_index_min) {
            a_index_min = p_b_index.getX();
        }
        if (p_c_index.getX() < a_index_min) {
            a_index_min = p_c_index.getX();
        }
        if (p_d_index.getX() < a_index_min) {
            a_index_min = p_d_index.getX();
        }

        return (int) Math.floor(a_index_min);
    }

    public int get_a_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のaベクトルの指数より大きい整数の指数を得る。
        Point p_a_index = new Point(getIndex(p_a));
        Point p_b_index = new Point(getIndex(p_b));
        Point p_c_index = new Point(getIndex(p_c));
        Point p_d_index = new Point(getIndex(p_d));

        double a_index_max = p_a_index.getX();
        if (p_b_index.getX() > a_index_max) {
            a_index_max = p_b_index.getX();
        }
        if (p_c_index.getX() > a_index_max) {
            a_index_max = p_c_index.getX();
        }
        if (p_d_index.getX() > a_index_max) {
            a_index_max = p_d_index.getX();
        }

        return (int) Math.ceil(a_index_max);
    }

    public int get_b_index_min(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より小さい整数の指数を得る。
        Point p_a_index = new Point(getIndex(p_a));
        Point p_b_index = new Point(getIndex(p_b));
        Point p_c_index = new Point(getIndex(p_c));
        Point p_d_index = new Point(getIndex(p_d));

        double b_index_min = p_a_index.getY();
        if (p_b_index.getY() < b_index_min) {
            b_index_min = p_b_index.getY();
        }
        if (p_c_index.getY() < b_index_min) {
            b_index_min = p_c_index.getY();
        }
        if (p_d_index.getY() < b_index_min) {
            b_index_min = p_d_index.getY();
        }

        return (int) Math.floor(b_index_min);
    }

    public int get_b_index_max(Point p_a, Point p_b, Point p_c, Point p_d) {//obj座標系の4つの点を指定し、各点のbベクトルの指数より大きい整数の指数を得る。
        Point p_a_index = new Point(getIndex(p_a));
        Point p_b_index = new Point(getIndex(p_b));
        Point p_c_index = new Point(getIndex(p_c));
        Point p_d_index = new Point(getIndex(p_d));

        double b_index_max = p_a_index.getY();
        if (p_b_index.getY() > b_index_max) {
            b_index_max = p_b_index.getY();
        }
        if (p_c_index.getY() > b_index_max) {
            b_index_max = p_c_index.getY();
        }
        if (p_d_index.getY() > b_index_max) {
            b_index_max = p_d_index.getY();
        }

        return (int) Math.ceil(b_index_max);
    }

    public Color getGridColor() {
        return grid_color;
    }

    public void setGridColor(Color color0) {
        grid_color = color0;
    }

    public Color getGridScaleColor() {
        return gridScaleColor;
    }

    public void setGridScaleColor(Color color0) {
        gridScaleColor = color0;
    }

    //描画-----------------------------------------------------------------
    public void draw(Graphics g, Camera camera, int p0x_max, int p0y_max, boolean colorChange) {    //colorChange=1なら一定数ごとに格子線の色を変える
        //入力規定が1か2（正方格子）の場合の格子線の描画
        Graphics2D g2 = (Graphics2D) g;

        LineSegment s_tv = new LineSegment();
        LineSegment s_ob = new LineSegment();

        //格子線の描画
        g2.setStroke(new BasicStroke((float) gridLineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));//線の太さや線の末端の形状

        if (baseState == State.HIDDEN) {
            return;
        }

        Point p0_a = new Point(0, 0);//TV system coordinates of the upper left corner of the screen
        Point p0_b = new Point(0, p0y_max);//TV system coordinates of the lower left corner of the screen
        Point p0_c = new Point(p0x_max, p0y_max);//TV system coordinates of corner at the bottom right of the screen
        Point p0_d = new Point(p0x_max, 0);//TV system coordinates of corner on the upper right of the screen

        Point p_a = new Point(camera.TV2object(p0_a));//Obj system coordinates of the upper left corner of the screen
        Point p_b = new Point(camera.TV2object(p0_b));//Obj system coordinates of the lower left corner of the screen
        Point p_c = new Point(camera.TV2object(p0_c));//Obj system coordinates of the lower right corner of the screen
        Point p_d = new Point(camera.TV2object(p0_d));//Obj system coordinates of the upper right corner of the screen

        int grid_screen_a_max = get_a_index_max(p_a, p_b, p_c, p_d);
        int grid_screen_a_min = get_a_index_min(p_a, p_b, p_c, p_d);
        int grid_screen_b_max = get_b_index_max(p_a, p_b, p_c, p_d);
        int grid_screen_b_min = get_b_index_min(p_a, p_b, p_c, p_d);

        //-------------------------------------
        if (baseState == State.WITHIN_PAPER) {
            int grid_yousi_x_max = divisionNumber();
            int grid_yousi_x_min = 0;
            int grid_yousi_y_max = divisionNumber();
            int grid_yousi_y_min = 0;

            if (grid_screen_a_max > grid_yousi_x_max) {
                grid_screen_a_max = grid_yousi_x_max;
            }
            if (grid_screen_a_min < grid_yousi_x_min) {
                grid_screen_a_min = grid_yousi_x_min;
            }
            if (grid_screen_b_max > grid_yousi_y_max) {
                grid_screen_b_max = grid_yousi_y_max;
            }
            if (grid_screen_b_min < grid_yousi_y_min) {
                grid_screen_b_min = grid_yousi_y_min;
            }

        }

        g.setColor(grid_color);
        for (int i = grid_screen_a_min; i <= grid_screen_a_max; i++) {
            s_ob.set(d_grid_ax * i + d_grid_bx * grid_screen_b_min + okx0,
                    d_grid_ay * i + d_grid_by * grid_screen_b_min + oky0,
                    d_grid_ax * i + d_grid_bx * grid_screen_b_max + okx0,
                    d_grid_ay * i + d_grid_by * grid_screen_b_max + oky0);

            s_tv.set(camera.object2TV(s_ob));
            g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
        }

        for (int i = grid_screen_b_min; i <= grid_screen_b_max; i++) {
            s_ob.set(d_grid_ax * grid_screen_a_min + d_grid_bx * i + okx0,
                    d_grid_ay * grid_screen_a_min + d_grid_by * i + oky0,
                    d_grid_ax * grid_screen_a_max + d_grid_bx * i + okx0,
                    d_grid_ay * grid_screen_a_max + d_grid_by * i + oky0);

            s_tv.set(camera.object2TV(s_ob));
            g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
        }

        //Change the color of the grid line for each constant -----------------------------------------------
        if (colorChange) {
            g.setColor(gridScaleColor);

            int i_balance;//剰余

            for (int i = grid_screen_a_min; i <= grid_screen_a_max; i++) {
                i_balance = i % b_to_parallel_scale_interval;
                if (i_balance < 0) {
                    i_balance = i_balance + b_to_parallel_scale_interval;
                }
                if (i_balance == b_to_parallel_scale_position) {
                    s_ob.set(d_grid_ax * i + d_grid_bx * grid_screen_b_min + okx0,
                            d_grid_ay * i + d_grid_by * grid_screen_b_min + oky0,
                            d_grid_ax * i + d_grid_bx * grid_screen_b_max + okx0,
                            d_grid_ay * i + d_grid_by * grid_screen_b_max + oky0);
                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                }
            }

            for (int i = grid_screen_b_min; i <= grid_screen_b_max; i++) {
                i_balance = i % a_to_parallel_scale_interval;
                if (i_balance < 0) {
                    i_balance = i_balance + a_to_parallel_scale_interval;
                }

                if (i_balance == a_to_parallel_scale_position) {

                    s_ob.set(d_grid_ax * grid_screen_a_min + d_grid_bx * i + okx0,
                            d_grid_ay * grid_screen_a_min + d_grid_by * i + oky0,
                            d_grid_ax * grid_screen_a_max + d_grid_bx * i + okx0,
                            d_grid_ay * grid_screen_a_max + d_grid_by * i + oky0);


                    s_tv.set(camera.object2TV(s_ob));
                    g.drawLine((int) s_tv.getAX(), (int) s_tv.getAY(), (int) s_tv.getBX(), (int) s_tv.getBY()); //直線
                }
            }
            //一定数ごとに格子線の色を変える　ここまで--------------------------------------------
        }
    }

    public Point closestGridPoint(Point t0) {
        Point t2 = new Point(); //格子点

        if (gridSize <= 0) {
            return t2;
        }

        if (baseState == State.HIDDEN) {
            return t2;
        }

        Point t_1 = new Point(t0.getX() - diagonal_max, t0.getY() - diagonal_max);
        Point t_2 = new Point(t0.getX() - diagonal_max, t0.getY() + diagonal_max);
        Point t_3 = new Point(t0.getX() + diagonal_max, t0.getY() + diagonal_max);
        Point t_4 = new Point(t0.getX() + diagonal_max, t0.getY() - diagonal_max);

        int grid_a_max = get_a_index_max(t_1, t_2, t_3, t_4);
        int grid_a_min = get_a_index_min(t_1, t_2, t_3, t_4);
        int grid_b_max = get_b_index_max(t_1, t_2, t_3, t_4);
        int grid_b_min = get_b_index_min(t_1, t_2, t_3, t_4);

        double distance_min = diagonal_max;
        for (int i = grid_a_min; i <= grid_a_max; i++) {
            for (int j = grid_b_min; j <= grid_b_max; j++) {
                Point t_tmp = new Point(okx0 + d_grid_ax * i + d_grid_bx * j, oky0 + d_grid_ay * i + d_grid_by * j);

                if (baseState == State.FULL || (baseState == State.WITHIN_PAPER && isWithinPaper(t_tmp))) {
                    if (t0.distance(t_tmp) <= distance_min) {
                        distance_min = t0.distance(t_tmp);
                        t2.set(t_tmp);
                    }
                }
            }
        }

        return t2;
    }

    private boolean isWithinPaper(Point t_tmp) {
        return ((-200.000001 <= t_tmp.getX()) && (t_tmp.getX() <= 200.000001)) && ((-200.000001 <= t_tmp.getY()) && (t_tmp.getY() <= 200.000001));
    }

    /**
     * The state of the grid, either within the paper, spanning the whole screen or hidden.
     */
    public enum State {
        HIDDEN(0),
        WITHIN_PAPER(1),
        FULL(2);

        int state;

        State(int state) {
            this.state = state;
        }

        public static State from(String state) {
            return from(Integer.parseInt(state));
        }

        public static State from(int state) {
            for (var val : State.values()) {
                if (val.getState() == state) {
                    return val;
                }
            }

            throw new IllegalArgumentException();
        }

        public State advance() {
            return values()[(ordinal() + 1) % values().length];
        }

        public int getState() {
            return state;
        }

        @Override
        public String toString() {
            return Integer.toString(state);
        }
    }
}
