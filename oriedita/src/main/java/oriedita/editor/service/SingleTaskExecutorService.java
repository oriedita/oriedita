package oriedita.editor.service;

import oriedita.editor.task.OrieditaTask;

import javax.inject.Singleton;
import java.util.concurrent.*;

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

    public void join() throws ExecutionException, InterruptedException, TimeoutException {
        currentTask.get(10, TimeUnit.SECONDS);
    }
}
