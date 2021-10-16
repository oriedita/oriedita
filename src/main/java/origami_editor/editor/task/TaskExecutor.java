package origami_editor.editor.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Can run one task at once.
 * TODO: Convert this to a singleton class
 */
public class TaskExecutor {
    static ExecutorService pool;
    private static Future<?> currentTask;

    static {
        pool = Executors.newFixedThreadPool(1);
    }

    public static void executeTask(Runnable runnable) {
        stopTask();

        currentTask = pool.submit(runnable);
    }

    public static boolean isTaskRunning() {
        return currentTask != null && !currentTask.isDone();
    }

    public static void stopTask() {
        if (currentTask != null && !currentTask.isDone()) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    pool.shutdownNow();

                    if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
                        System.err.println("Pool did not terminate!");
                    }
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }

            pool = Executors.newFixedThreadPool(1);
        }
    }
}
