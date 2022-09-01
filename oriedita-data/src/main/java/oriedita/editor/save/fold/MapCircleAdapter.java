package oriedita.editor.save.fold;

import fold.custom.Adapter;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCircleAdapter implements Adapter<Map<MapCircleAdapter.CircleFields, Object>, Circle> {
    @Override
    public Circle convert(Map<CircleFields, Object> map, Circle to) {
        Circle circle = new Circle();
        List<?> coord = (List<?>) map.get(CircleFields.circles_coords);
        BigDecimal radius = (BigDecimal) map.get(CircleFields.circles_radii);
        String color = (String) map.get(CircleFields.circles_colors);

        if (coord == null || radius == null) {
            return null;
        }

        if (color != null) {
            circle.setColor(LineColor.from(color));
        }
        circle.setR(radius.doubleValue());
        BigDecimal x = (BigDecimal) coord.get(0);
        BigDecimal y = (BigDecimal) coord.get(1);

        circle.setX(x.doubleValue());
        circle.setY(y.doubleValue());

        return circle;
    }

    @Override
    public Map<CircleFields, Object> convertBack(Circle circle, Map<CircleFields, Object> to) {
        Map<CircleFields, Object> output = new HashMap<>();
        output.put(CircleFields.circles_coords, Arrays.asList(BigDecimal.valueOf(circle.getX()), BigDecimal.valueOf(circle.getY())));
        output.put(CircleFields.circles_radii, BigDecimal.valueOf(circle.getR()));
        output.put(CircleFields.circles_colors, circle.getColor().toString());

        return output;
    }

    public enum CircleFields {
        circles_coords, circles_radii, circles_colors
    }
}
