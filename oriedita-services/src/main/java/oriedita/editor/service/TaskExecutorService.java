package oriedita.editor.service;

import oriedita.editor.task.OrieditaTask;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface TaskExecutorService {
    String getTaskName();

    void executeTask(OrieditaTask task);

    boolean isTaskRunning();

    void stopTask();

    void join() throws ExecutionException, InterruptedException, TimeoutException;
}
