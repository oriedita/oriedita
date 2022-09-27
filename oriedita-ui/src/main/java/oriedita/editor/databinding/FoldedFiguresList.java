package oriedita.editor.databinding;

import oriedita.editor.drawing.FoldedFigure_Drawer;

import javax.inject.Inject;
import javax.inject.Singleton;
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

    public FoldedFigure_Drawer[] getItems() {
        FoldedFigure_Drawer[] list = new FoldedFigure_Drawer[getSize()];
        for (int i = 0; i < getSize(); i++) {
            list[i] = getElementAt(i);
        }

        return list;
    }
}
