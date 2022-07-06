package oriedita.editor.databinding;

import javax.inject.Inject;
import javax.inject.Singleton;
import oriedita.editor.drawing.FoldedFigure_Drawer;

import javax.swing.*;

@Singleton
public class FoldedFiguresList extends DefaultComboBoxModel<FoldedFigure_Drawer> {
    @Inject
    public FoldedFiguresList() {
    }

    public FoldedFigure_Drawer getActiveItem() {
        Object selectedItem = getSelectedItem();

        if (selectedItem != null) {
            return (FoldedFigure_Drawer) selectedItem;
        }

        return null;
    }
}
