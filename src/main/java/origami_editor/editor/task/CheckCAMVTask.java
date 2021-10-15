package origami_editor.editor.task;

import origami_editor.editor.App;

public class CheckCAMVTask implements Runnable{
    private final App app;

    public CheckCAMVTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        try {
            app.mainCreasePatternWorker.ap_check4(app.d_ap_check4);
        } catch (InterruptedException e) {
            app.mainCreasePatternWorker.foldLineSet.getCheck4LineSegments().clear();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.repaint();
    }
}
