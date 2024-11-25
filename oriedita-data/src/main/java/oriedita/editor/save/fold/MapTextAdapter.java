package oriedita.editor.save.fold;

import fold.custom.Adapter;
import oriedita.editor.text.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapTextAdapter implements Adapter<Map<MapTextAdapter.TextFields, Object>, Text> {
    @Override
    public Text convert(Map<MapTextAdapter.TextFields, Object> map, Text to) {
        List<?> coord = (List<?>) map.get(MapTextAdapter.TextFields.texts_coords);
        String textVal = (String) map.get(TextFields.texts_text);

        if (coord == null || textVal == null) {
            return null;
        }

        double x = (double) coord.get(0);
        double y = (double) coord.get(1);

        return new Text(x, y, textVal);
    }

    @Override
    public Map<MapTextAdapter.TextFields, Object> convertBack(Text text, Map<MapTextAdapter.TextFields, Object> to) {
        Map<MapTextAdapter.TextFields, Object> output = new HashMap<>();
        output.put(MapTextAdapter.TextFields.texts_coords, Arrays.asList(text.getX(), text.getY()));
        output.put(MapTextAdapter.TextFields.texts_text, text.getText());

        return output;
    }

    public enum TextFields {
        texts_coords, texts_text
    }
}
