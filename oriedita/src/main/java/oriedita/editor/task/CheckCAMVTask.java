package oriedita.editor.task;

import org.tinylog.Logger;
import oriedita.editor.databinding.CanvasModel;
import origami.crease_pattern.FoldLineSet;

public class CheckCAMVTask implements OrieditaTask {
    private final FoldLineSet foldLineSet;
    private final CanvasModel canvasModel;

    public CheckCAMVTask(FoldLineSet foldLineSet, CanvasModel canvasModel) {
        this.foldLineSet = foldLineSet;
        this.canvasModel = canvasModel;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        try {
            foldLineSet.check4();
        } catch (InterruptedException e) {
            foldLineSet.getViolations().clear();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        Logger.info("Check4 computation time " + L + " msec.");

        canvasModel.markDirty();
    }

    @Override
    public String getName() {
        return "camv";
    }
}
