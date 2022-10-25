package oriedita.editor.tools;

import java.awt.Color;

public class StringOp {//データ格納用のメモ帳

    public static int String2int(String str0, int henkan_error_modoriti) {

        int s_decision;
        s_decision = String2suuti_decision(str0);

        if (s_decision == 1) {
            return Integer.parseInt(str0);
        }
        if (s_decision == 2) {
            double d0;
            d0 = Double.parseDouble(str0);
            return (int) Math.round(d0);
        }

        return henkan_error_modoriti;
    }

    public static double String2double(String str0, double henkan_error_modoriti) {

        int s_decision;
        s_decision = String2suuti_decision(str0);

        if (s_decision == 1) {
            return Integer.parseInt(str0);
        }
        if (s_decision == 2) {
            return Double.parseDouble(str0);
        }

        return henkan_error_modoriti;
    }

    public static int String2int_confirm(String str0) {//stringがintに変換できるかどうかを確認する。できる=1。できない=0
        try {
            Integer.parseInt(str0);
            return 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int String2double_confirm(String str0) {//stringがdoubleに変換できるかどうかを確認する。できる=1。できない=0
        try {
            Double.parseDouble(str0);
            return 1;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int String2suuti_decision(String str0) {//stringを数値としてみた場合に型を判定する。double=2。int=1。数値ではない=0
        int i_r = 0;
        if (String2double_confirm(str0) == 1) {
            i_r = 2;
        }
        if (String2int_confirm(str0) == 1) {
            i_r = 1;
        }
        return i_r;
    }

    // ---------------------------------

    public static String toHtmlColor(Color col) {
        // Returns white if null.
        if (col == null) {
            return "#FFFFFF";
        }
        String str;
        String colCode;

        str = "#";

        // Get the red component in hexadecimal, set
        colCode = Integer.toHexString(col.getRed());
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;
        // Get the green component in hexadecimal, set
        colCode = Integer.toHexString(col.getGreen());
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;

        // Get the blue component in hexadecimal, set
        colCode = Integer.toHexString(col.getBlue());
        if (colCode.length() == 1) {
            colCode = "0" + colCode;
        }
        str = str + colCode;

        return str;
    }

    public static boolean isEmpty(String val) {
        return val == null || val.isEmpty();
    }
}
