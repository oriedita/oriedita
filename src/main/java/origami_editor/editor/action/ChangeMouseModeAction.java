package origami_editor.editor.action;

import origami_editor.editor.App;
import origami_editor.editor.MouseMode;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ChangeMouseModeAction extends AbstractAction {
    private final App app;
    private final MouseMode mouseMode;
    private final String helpKey;

    public ChangeMouseModeAction(App app, MouseMode mouseMode, String helpKey, String name, int mnemonicKey) {
        super(name);
        this.app = app;
        this.mouseMode = mouseMode;
        this.helpKey = helpKey;
        putValue(ACCELERATOR_KEY, mnemonicKey);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        app.setHelp(this.helpKey);

        app.canvasModel.setMouseMode(this.mouseMode);

        app.Button_shared_operation();
        app.repaintCanvas();
    }
}
