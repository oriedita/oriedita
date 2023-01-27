package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.task.OrieditaTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class SingleTaskExecutorServiceImpl implements TaskExecutorService {
    private final ExecutorService pool;
    private static Future<?> currentTask = CompletableFuture.completedFuture(null);

    private static String taskName = "";

    public SingleTaskExecutorServiceImpl() {
        pool = Executors.newFixedThreadPool(1);
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    @Override
    public void executeTask(OrieditaTask task) {
        stopTask();

        taskName = task.getName();

        currentTask = pool.submit(task);
    }

    @Override
    public boolean isTaskRunning() {
        return !currentTask.isDone();
    }

    @Override
    public void stopTask() {
        if (isTaskRunning()) {
            currentTask.cancel(true);
        }
    }

    @Override
    public void join() throws ExecutionException, InterruptedException, TimeoutException {
        currentTask.get(10, TimeUnit.SECONDS);
    }
}
