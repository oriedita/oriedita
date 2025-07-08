package oriedita.editor.save.fold;

import fold.custom.Adapter;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCircleAdapter implements Adapter<Map<MapCircleAdapter.CircleFields, Object>, Circle> {
    @Override
    public Circle convert(Map<CircleFields, Object> map, Circle to) {
        Circle circle = new Circle();
        List<?> coord = (List<?>) map.get(CircleFields.circles_coords);
        Double radius = CastUtil.toDouble(map.get(CircleFields.circles_radii));
        String color = (String) map.get(CircleFields.circles_colors);
        String customColor = (String) map.get(CircleFields.circles_custom_colors);

        if (coord == null || radius == null) {
            return null;
        }

        if (color != null) {
            circle.setColor(LineColor.from(color));
        }
        if (customColor != null && !customColor.isEmpty()) {
            circle.setCustomized(1);
            circle.setCustomizedColor(Color.decode("#" + customColor));
        }
        circle.setR(radius);
        double x = CastUtil.toDouble(coord.get(0));
        double y = CastUtil.toDouble(coord.get(1));

        circle.setX(x);
        circle.setY(y);

        return circle;
    }

    @Override
    public Map<CircleFields, Object> convertBack(Circle circle, Map<CircleFields, Object> to) {
        Map<CircleFields, Object> output = new HashMap<>();
        output.put(CircleFields.circles_coords, Arrays.asList(circle.getX(), circle.getY()));
        output.put(CircleFields.circles_radii, circle.getR());
        output.put(CircleFields.circles_colors, circle.getColor().toString());
        output.put(CircleFields.circles_custom_colors, circle.getCustomized() == 1?
                Integer.toHexString(circle.getCustomizedColor().getRGB()).substring(2) : "");
        return output;
    }

    public enum CircleFields {
        circles_coords, circles_radii, circles_colors, circles_custom_colors
    }
}
