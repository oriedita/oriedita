package oriedita.editor.action.selector;

import java.awt.event.MouseEvent;

/**
 * To decouple selectors from AWT as much as possible
 */
public class MouseEventInfo {
    private final boolean isCtrlDown;

    public MouseEventInfo(MouseEvent e) {
        this.isCtrlDown = e.isControlDown();
    }

    public boolean isCtrlDown() {
        return isCtrlDown;
    }
}
