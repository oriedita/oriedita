package origami_editor.editor.canvas;

import java.util.EnumSet;

public abstract class BaseMouseHandler implements MouseModeHandler {
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }

    public void setDrawingWorker(CreasePattern_Worker d) {
        this.d = d;
    }
}
