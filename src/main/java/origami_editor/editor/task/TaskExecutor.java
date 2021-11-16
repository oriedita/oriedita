package origami_editor.editor.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Can run one task at once.
 * TODO: Convert this to a singleton class
 */
public class TaskExecutor {
    static ExecutorService pool;
    private static Future<?> currentTask = new FinishedFuture<>(null);

    private static String taskName = "";

    static {
        pool = Executors.newFixedThreadPool(1);
    }

    public static String getTaskName() {
        return taskName;
    }

    public static void executeTask(String name, Runnable runnable) {
        stopTask();

        taskName = name;

        currentTask = pool.submit(runnable);
    }

    public static boolean isTaskRunning() {
        return !currentTask.isDone();
    }

    public static void stopTask() {
        if (isTaskRunning()) {
            currentTask.cancel(true);
        }
    }
}
