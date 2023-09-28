package oriedita.editor.handler;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public abstract class PopupMenuAdapter implements PopupMenuListener {
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // Empty default implementation
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // Empty default implementation
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // Empty default implementation
    }
}

