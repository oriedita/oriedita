package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;

public class TaskFactory {
    @Named("camvExecutor")
    @Produces
    @ApplicationScoped
    public TaskExecutorService camvTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }

    @Named("foldingExecutor")
    @Produces
    @ApplicationScoped
    public TaskExecutorService foldingTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }
}
