package origami_editor.editor.task;

import origami_editor.editor.Canvas;
import origami_editor.editor.canvas.CreasePattern_Worker;

public class CheckCAMVTask implements Runnable{
    private final CreasePattern_Worker creasePattern_worker;
    private final Canvas canvas;

    public CheckCAMVTask(CreasePattern_Worker creasePattern_worker, Canvas canvas) {
        this.creasePattern_worker = creasePattern_worker;
        this.canvas = canvas;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        try {
            creasePattern_worker.ap_check4();
        } catch (InterruptedException e) {
            creasePattern_worker.foldLineSet.getCheck4LineSegments().clear();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        System.out.println("Check4 computation time " + L + " msec.");

        canvas.repaint();
    }
}
