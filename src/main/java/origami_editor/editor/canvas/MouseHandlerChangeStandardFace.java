package origami_editor.editor.canvas;

import org.springframework.stereotype.Component;
import origami.crease_pattern.element.Point;
import origami_editor.editor.MouseMode;
import origami.folding.FoldedFigure;
import origami_editor.editor.databinding.FoldedFiguresList;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

import javax.swing.*;

@Component
class MouseHandlerChangeStandardFace extends BaseMouseHandler {
    private final CreasePattern_Worker d;
    private final FoldedFiguresList foldedFiguresList;

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

            System.out.println("kijyunmen_id = " + newStartingFaceId);
            if (selectedFigure.foldedFigure.ct_worker.face_rating != null) {//20180227追加
                System.out.println(
                        "OZ.js.nbox.get_jyunjyo = " + selectedFigure.foldedFigure.ct_worker.nbox.getSequence(newStartingFaceId) + " , rating = " +
                                selectedFigure.foldedFigure.ct_worker.nbox.getWeight(selectedFigure.foldedFigure.ct_worker.nbox.getSequence(newStartingFaceId))

                );

            }
            if ((newStartingFaceId != oldStartingFaceId) && (selectedFigure.foldedFigure.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
                selectedFigure.foldedFigure.estimationStep = FoldedFigure.EstimationStep.STEP_1;
            }
        }
    }
}
