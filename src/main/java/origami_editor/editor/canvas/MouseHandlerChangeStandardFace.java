package origami_editor.editor.canvas;

import origami.crease_pattern.element.Point;
import origami_editor.editor.App;
import origami_editor.editor.MouseMode;
import origami.folding.FoldedFigure;
import origami_editor.editor.drawing.FoldedFigure_Drawer;

public class MouseHandlerChangeStandardFace implements MouseModeHandler {
    private final App app;

    public MouseHandlerChangeStandardFace(App app) {
        this.app = app;
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

        FoldedFigure_Drawer selectedFigure = (FoldedFigure_Drawer) app.foldedFiguresList.getSelectedItem();

        if (selectedFigure != null) {
            int new_referencePlane_id;
            int old_referencePlane_id;
            old_referencePlane_id = selectedFigure.foldedFigure.cp_worker1.getReferencePlaneId();

            new_referencePlane_id = selectedFigure.wireFrame_worker_drawer1.setReferencePlaneId(p0);
            System.out.println("kijyunmen_id = " + new_referencePlane_id);
            if (selectedFigure.foldedFigure.ct_worker.face_rating != null) {//20180227追加
                System.out.println(
                        "OZ.js.nbox.get_jyunjyo = " + selectedFigure.foldedFigure.ct_worker.nbox.getSequence(new_referencePlane_id) + " , rating = " +
                                selectedFigure.foldedFigure.ct_worker.nbox.getWeight(selectedFigure.foldedFigure.ct_worker.nbox.getSequence(new_referencePlane_id))

                );

            }
            if ((new_referencePlane_id != old_referencePlane_id) && (selectedFigure.foldedFigure.estimationStep != FoldedFigure.EstimationStep.STEP_0)) {
                selectedFigure.foldedFigure.estimationStep = FoldedFigure.EstimationStep.STEP_1;
            }
        }
    }
}
