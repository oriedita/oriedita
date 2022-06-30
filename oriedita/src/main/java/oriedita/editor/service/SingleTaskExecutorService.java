package oriedita.editor.service;

import oriedita.editor.task.OrieditaTask;

import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Singleton
public class SingleTaskExecutorService {
    private final ExecutorService pool;
    private static Future<?> currentTask = CompletableFuture.completedFuture(null);

    private static String taskName = "";

    public SingleTaskExecutorService() {
        pool = Executors.newFixedThreadPool(1);
    }

    public String getTaskName() {
        return taskName;
    }

    public void executeTask(OrieditaTask task) {
        stopTask();

        taskName = task.getName();

        currentTask = pool.submit(task);
    }

    public boolean isTaskRunning() {
        return !currentTask.isDone();
    }

    public void stopTask() {
        if (isTaskRunning()) {
            currentTask.cancel(true);
        }
    }
}
