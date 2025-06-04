package oriedita.editor.save;

import fold.custom.CustomListField;
import fold.model.FoldFile;
import oriedita.editor.databinding.GridModel;
import oriedita.editor.save.fold.MapCircleAdapter;
import oriedita.editor.save.fold.MapTextAdapter;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Circle;

import java.util.List;

public class OrieditaFoldFile extends FoldFile {
    private static final String NS = "oriedita";
    private final CustomListField<Text, MapTextAdapter.TextFields> textFieldsCustomListField;
    private final CustomListField<Circle, MapCircleAdapter.CircleFields> circleFieldsCustomListField;

    public OrieditaFoldFile() {
        circleFieldsCustomListField = new CustomListField<>(NS, MapCircleAdapter.CircleFields.class, new MapCircleAdapter());
        textFieldsCustomListField = new CustomListField<>(NS, MapTextAdapter.TextFields.class, new MapTextAdapter());
    }

    /**
     * Retrieve circles from custom properties in embedded FoldFile.
     */
    public List<Circle> getCircles() {
        return circleFieldsCustomListField.getValue(getCustomPropertyMap());
    }

    /**
     * Set circles in custom properties of embedded FoldFile
     */
    public void setCircles(List<Circle> circles) {
        circleFieldsCustomListField.setValue(getCustomPropertyMap(), circles);
    }

    public List<Text> getTexts() {
        return textFieldsCustomListField.getValue(getCustomPropertyMap());
    }

    public void setTexts(List<Text> texts) {
        textFieldsCustomListField.setValue(getCustomPropertyMap(), texts);
    }

    public String getVersion() {
        return (String) getCustomProperty(NS, "version");
    }

    public void setVersion(String version) {
        setCustomProperty(NS, "version", version);
    }

    public void setGridSize(int gridSize) {
        setCustomProperty(NS, "grid_size", gridSize);
    }
    public int getGridSize() {
        var size = (Integer) getCustomProperty(NS, "grid_size");
        return size == null? 8 : size;
    }
    public void setGridStyle(GridModel.State gridStyle) {
        setCustomProperty(NS, "grid_style", gridStyle.getState());
    }
    public GridModel.State getGridStyle() {
        var style = (Integer) getCustomProperty(NS, "grid_style");
        return style == null? GridModel.State.HIDDEN : GridModel.State.from((int) getCustomProperty(NS, "grid_style"));
    }
}
