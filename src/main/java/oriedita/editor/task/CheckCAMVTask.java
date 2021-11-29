package oriedita.editor.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oriedita.editor.canvas.CreasePattern_Worker;
import oriedita.editor.databinding.CanvasModel;

import java.util.concurrent.*;

public class CheckCAMVTask implements Callable<Void> {
    private static final Logger logger = LogManager.getLogger(CheckCAMVTask.class);
    private final CreasePattern_Worker creasePattern_worker;
    private final CanvasModel canvasModel;

    private static final ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(1);
    }

    public static Future<?> execute(CreasePattern_Worker creasePattern_worker, CanvasModel canvasModel) {
        return executor.submit(new CheckCAMVTask(creasePattern_worker, canvasModel));
    }

    public CheckCAMVTask(CreasePattern_Worker creasePattern_worker, CanvasModel canvasModel) {
        this.creasePattern_worker = creasePattern_worker;
        this.canvasModel = canvasModel;
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
        logger.info("Check4 computation time " + L + " msec.");

        canvasModel.markDirty();

        return null;
    }
}
