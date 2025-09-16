package oriedita.editor.handler.step;

import oriedita.editor.drawing.tools.Camera;

// this is only there for compatibility with the old system without a stepFactory,
// should be removed after all handlers are migrated
// TODO: switch all mousehandlers over to used initStepGraph method instead of initializing in the constructor
@Deprecated
public interface ICameraStepNode {
    void setCamera(Camera camera);
}
