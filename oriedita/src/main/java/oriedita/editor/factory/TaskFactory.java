package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;

import javax.inject.Named;

@Module
public class TaskFactory {
    @Named("camvExecutor")
    @Provides
    TaskExecutorService camvTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }

    @Named("foldingExecutor")
    @Provides
    TaskExecutorService foldingTaskExecutorService() {
        return new SingleTaskExecutorServiceImpl();
    }
}
