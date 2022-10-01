package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;

@ApplicationScoped
public class TaskFactory {
    @Named("camvExecutor")
    @Produces
    public TaskExecutorService camvTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }

    @Named("foldingExecutor")
    @Produces
    public TaskExecutorService foldingTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }
}
