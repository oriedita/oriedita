package oriedita.editor.action;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.tinylog.Logger;
import origami.crease_pattern.element.Point;
import oriedita.editor.canvas.MouseMode;
import origami.folding.FoldedFigure;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.FoldedFiguresList;
import oriedita.editor.drawing.FoldedFigure_Drawer;

@Singleton
public class MouseHandlerChangeStandardFace extends BaseMouseHandler {
    private final CreasePattern_Worker d;
    private final FoldedFiguresList foldedFiguresList;

    @Inject
    public MouseHandlerChangeStandardFace(FoldedFiguresList foldedFiguresList, CreasePattern_Worker d) {
        this.foldedFiguresList = foldedFiguresList;
        this.d = d;
    }

    @Override
    public MouseMode getMouseMode() {
        return MouseMode.CHANGE_STANDARD_FACE_103;
    }

    @Override
    public void mouseMoved(Point p0) {

    }

    @Override
    public void mousePressed(Point p0) {

    }

    @Override
    public void mouseDragged(Point p0) {

    }

    @Override
    public void mouseReleased(Point p0) {

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) foldedFiguresList.getSelectedItem();

        if (selectedFigure != null) {
            Point p = new Point();
            p.set(d.camera.TV2object(p0));
            int oldStartingFaceId = selectedFigure.getStartingFaceId();

            int newStartingFaceId = selectedFigure.foldedFigure.cp_worker1.get().inside(p);

            if (newStartingFaceId < 1) return;

            selectedFigure.setStartingFaceId(newStartingFaceId);

            Logger.info("kijyunmen_id = " + newStartingFaceId);
            if (selectedFigure.foldedFigure.ct_worker.face_rating != null) {//20180227追加
                int index = selectedFigure.foldedFigure.ct_worker.nbox.getSequence(newStartingFaceId);
                Logger.info(
                        "OZ.js.nbox.get_jyunjyo = " + index + " , rating = " + selectedFigure.foldedFigure.ct_worker.nbox.getWeight(index)
                );

            }
            if ((newStartingFaceId != oldStartingFaceId) && (selectedFigure.foldedFigure.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
                selectedFigure.foldedFigure.estimationStep = FoldedFigure.EstimationStep.STEP_1;
            }
        }
    }
}
