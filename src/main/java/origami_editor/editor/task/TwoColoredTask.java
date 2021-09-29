package origami_editor.editor.task;

import origami_editor.editor.App;

public class TwoColoredTask implements Runnable{
    private final App app;

    public TwoColoredTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        try {
            app.createTwoColorCreasePattern();
        } catch (InterruptedException e) {
            app.OZ.estimated_initialize();
            e.printStackTrace();
        }

        long stop = System.currentTimeMillis();
        long L = stop - start;
        app.OZ.text_result = app.OZ.text_result + "     Computation time " + L + " msec.";

        app.repaint();
    }
}
