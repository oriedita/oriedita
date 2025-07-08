package oriedita.editor.save.fold;

import fold.custom.Adapter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MapColorAdapter implements Adapter<Map<MapColorAdapter.ColorFields, Object>, Optional<Color>> {
    @Override
    public Optional<Color> convert(Map<ColorFields, Object> colorFieldsObjectMap, Optional<Color> color) {
        String c = (String) colorFieldsObjectMap.get(ColorFields.edges_colors);
        if (Objects.equals(c, "")) {
            return Optional.empty();
        }
        return Optional.of(Color.decode("#" + c));
    }

    @Override
    public Map<ColorFields, Object> convertBack(Optional<Color> color, Map<ColorFields, Object> colorFieldsObjectMap) {
        var map = new HashMap<ColorFields, Object>();
        if (color.isEmpty()) {
            map.put(ColorFields.edges_colors, "");
        } else {
            map.put(ColorFields.edges_colors, Integer.toHexString(color.get().getRGB()).substring(2));
        }
        return map;
    }

    public enum ColorFields {
        edges_colors
    }
}
