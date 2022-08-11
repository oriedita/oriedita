package oriedita.editor.save;

import fold.model.FoldFile;
import org.tinylog.Logger;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Circle;
import origami.crease_pattern.element.LineColor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrieditaFoldFile extends FoldFile {
    private static final String NS = "oriedita";
    public static final String KEY_CIRCLES_COORDS = "circles_coords";
    public static final String KEY_CIRCLES_RADII = "circles_radii";
    public static final String KEY_CIRCLES_COLORS = "circles_colors";
    public static final String KEY_TEXTS_COORDS = "texts_coords";
    public static final String KEY_TEXTS_TEXT = "texts_text";

    public OrieditaFoldFile() {
        // Public empty constructor required.
    }

    /**
     * Retrieve circles from custom properties in embedded FoldFile.
     */
    public List<Circle> getCircles() {
        Object coords = getCustomProperty(NS, KEY_CIRCLES_COORDS);
        Object radii = getCustomProperty(NS, KEY_CIRCLES_RADII);
        Object colors = getCustomProperty(NS, KEY_CIRCLES_COLORS);

        if (coords == null || radii == null || colors == null) {
            // No circles present
            return Collections.emptyList();
        }

        try {
            List<?> coordsListU = (List<?>) coords;
            List<?> radiiListU = (List<?>) radii;
            List<?> colorsListU = (List<?>) colors;

            List<Circle> circles = new ArrayList<>();

            if (coordsListU.size() == 0) {
                return Collections.emptyList();
            }

            if (radiiListU.size() != coordsListU.size() || colorsListU.size() != coordsListU.size()) {
                Logger.warn("oriedita:circles_ not all parts of equal length.");
                return Collections.emptyList();
            }

            for (int i = 0; i < coordsListU.size(); i++) {
                List<?> coord = (List<?>) coordsListU.get(i);
                BigDecimal radius = (BigDecimal) radiiListU.get(i);
                String color = (String) colorsListU.get(i);

                Circle circle = new Circle();

                circle.setR(radius.doubleValue());
                circle.setColor(LineColor.from(color));

                BigDecimal x = (BigDecimal) coord.get(0);
                BigDecimal y = (BigDecimal) coord.get(1);

                circle.setX(x.doubleValue());
                circle.setY(y.doubleValue());

                circles.add(circle);
            }

            // Users cannot modify this list as it will get out of sync.
            return Collections.unmodifiableList(circles);
        } catch (ClassCastException | ArrayIndexOutOfBoundsException ex) {
            // Catch any errors in the format of the json map.
            Logger.warn(ex, "Encountered error in fold file while parsing circles");

            return Collections.emptyList();
        }
    }

    /**
     * Set circles in custom properties of embedded FoldFile
     */
    public void setCircles(List<Circle> circles) {
        if (circles.size() == 0) {
            removeCustomProperty(NS, KEY_CIRCLES_COORDS);
            removeCustomProperty(NS, KEY_CIRCLES_RADII);
            removeCustomProperty(NS, KEY_CIRCLES_COLORS);

            return;
        }

        List<List<BigDecimal>> coords = new ArrayList<>();
        List<BigDecimal> radii = new ArrayList<>();
        List<String> colors = new ArrayList<>();
        for (Circle circle : circles) {
            coords.add(Arrays.asList(BigDecimal.valueOf(circle.getX()), BigDecimal.valueOf(circle.getY())));
            radii.add(BigDecimal.valueOf(circle.getR()));
            colors.add(circle.getColor().toString());
        }

        setCustomProperty(NS, KEY_CIRCLES_COORDS, coords);
        setCustomProperty(NS, KEY_CIRCLES_RADII, radii);
        setCustomProperty(NS, KEY_CIRCLES_COLORS, colors);
    }

    public List<Text> getTexts() {
        Object coords = getCustomProperty(NS, KEY_TEXTS_COORDS);
        Object values = getCustomProperty(NS, KEY_TEXTS_TEXT);

        if (coords == null || values == null) {
            return Collections.emptyList();
        }

        try {
            List<?> coordsListU = (List<?>) coords;
            List<?> valueListU = (List<?>) values;

            List<Text> texts = new ArrayList<>();

            if (coordsListU.size() == 0) {
                return Collections.emptyList();
            }

            if (valueListU.size() != coordsListU.size()) {
                Logger.warn("oriedita:text_ not all parts of equal length.");
                return Collections.emptyList();
            }

            for (int i = 0; i < coordsListU.size(); i++) {
                List<?> coord = (List<?>) coordsListU.get(i);
                String value = (String) valueListU.get(i);

                BigDecimal x = (BigDecimal) coord.get(0);
                BigDecimal y = (BigDecimal) coord.get(1);

                Text text = new Text(x.doubleValue(), y.doubleValue(), value);

                texts.add(text);
            }

            // Users cannot modify this list as it will get out of sync.
            return Collections.unmodifiableList(texts);

        } catch (ClassCastException ex) {
            Logger.warn(ex, "Encountered error in fold file while parsing texts");

            return Collections.emptyList();
        }
    }

    public void setTexts(List<Text> texts) {
        if (texts.size() == 0) {
            removeCustomProperty(NS, KEY_TEXTS_COORDS);
            removeCustomProperty(NS, KEY_TEXTS_TEXT);

            return;
        }

        List<List<BigDecimal>> coords = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for (Text circle : texts) {
            coords.add(Arrays.asList(BigDecimal.valueOf(circle.getX()), BigDecimal.valueOf(circle.getY())));
            values.add(circle.getText());
        }

        setCustomProperty(NS, KEY_TEXTS_COORDS, coords);
        setCustomProperty(NS, KEY_TEXTS_TEXT, values);
    }
}
