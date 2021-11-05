package origami_editor.editor.task;

import origami_editor.editor.Canvas;
import origami_editor.editor.canvas.CreasePattern_Worker;

import java.util.concurrent.*;

public class CheckCAMVTask implements Callable<Void> {
    private final CreasePattern_Worker creasePattern_worker;
    private final Canvas canvas;

    private static final ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(1);
    }

    public static Future<?> execute(CreasePattern_Worker creasePattern_worker, Canvas canvas) {
        return executor.submit(new CheckCAMVTask(creasePattern_worker, canvas));
    }

    public CheckCAMVTask(CreasePattern_Worker creasePattern_worker, Canvas canvas) {
        this.creasePattern_worker = creasePattern_worker;
        this.canvas = canvas;
    }

    @Override
    public Void call() {
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

        return null;
    }
}
