package oriedita.editor.action;


import oriedita.editor.canvas.CreasePattern_Worker;

import javax.inject.Inject;

import java.util.EnumSet;

public abstract class BaseMouseHandler implements MouseModeHandler {
    @Inject
    protected CreasePattern_Worker d;

    public BaseMouseHandler() { }

    @Override
    public EnumSet<Feature> getSubscribedFeatures() {
        return EnumSet.of(Feature.BUTTON_1);
    }
}
