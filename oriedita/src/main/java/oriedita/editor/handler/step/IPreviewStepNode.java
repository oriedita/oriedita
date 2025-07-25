package oriedita.editor.handler.step;

import oriedita.editor.drawing.tools.Camera;
import oriedita.editor.handler.DrawingSettings;

import java.awt.Graphics2D;

public interface IPreviewStepNode {
    void drawPreview(Graphics2D g, Camera camera, DrawingSettings drawingSettings);
}
