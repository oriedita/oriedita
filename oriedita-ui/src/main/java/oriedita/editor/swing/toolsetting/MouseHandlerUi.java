package oriedita.editor.swing.toolsetting;

import oriedita.editor.handler.MouseHandlerSettingGroup;
import oriedita.editor.handler.UiFor;

import javax.swing.JComponent;

public interface MouseHandlerUi {
    void init();
    JComponent $$$getRootComponent$$$();

    default MouseHandlerSettingGroup getSettingGroup() {
        UiFor annotation = getClass().getAnnotation(UiFor.class);

        if (annotation == null){
            throw new IllegalStateException("MouseHandlerUi does not have an @UiFor annotation: " + getClass());
        }

        return annotation.value();
    }
}
