package oriedita.editor.save.fold;

public class CastUtil {
    public static Double toDouble(Object obj) {
        if (obj instanceof Double d) {
            return d;
        }
        if (obj instanceof Integer i) {
            return i.doubleValue();
        }
        return null;
    }
}
