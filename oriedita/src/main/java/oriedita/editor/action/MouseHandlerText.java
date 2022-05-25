package oriedita.editor.action;

import oriedita.editor.canvas.MouseMode;
import oriedita.editor.canvas.TextWorker;
import oriedita.editor.databinding.SelectedTextModel;
import oriedita.editor.text.Text;
import origami.crease_pattern.element.Point;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MouseHandlerText extends BaseMouseHandler{
    private final SelectedTextModel textModel;
    private final TextWorker textWorker;
    @Inject
    public MouseHandlerText(SelectedTextModel textModel,
                            TextWorker textWorker) {
        this.textModel = textModel;
        this.textWorker = textWorker;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.TEXT;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {
        Point p = d.camera.TV2object(p0);
        Text t = new Text(p.getX(), p.getY(), "Test");
        textWorker.addText(t);
        textModel.setSelectedText(t);
        textModel.setSelected(true);
    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {

    }
}
