package oriedita.editor.service.impl;

import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.task.OrieditaTask;

import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton
public class SingleTaskExecutorServiceImpl implements TaskExecutorService {
    private final ExecutorService pool;
    private static Future<?> currentTask = CompletableFuture.completedFuture(null);

    private static String taskName = "";

    public SingleTaskExecutorServiceImpl() {
        pool = Executors.newFixedThreadPool(1);
    }

    @Override public String getTaskName() {
        return taskName;
    }

    @Override public void executeTask(OrieditaTask task) {
        stopTask();

        taskName = task.getName();

        currentTask = pool.submit(task);
    }

    @Override public boolean isTaskRunning() {
        return !currentTask.isDone();
    }

    @Override public void stopTask() {
        if (isTaskRunning()) {
            currentTask.cancel(true);
        }
    }

    @Override public void join() throws ExecutionException, InterruptedException, TimeoutException {
        currentTask.get(10, TimeUnit.SECONDS);
    }
}
