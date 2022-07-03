package oriedita.editor.factory;

import dagger.Module;
import dagger.Provides;
import oriedita.editor.service.SingleTaskExecutorService;

import javax.inject.Named;

@Module
public class TaskFactory {
    @Named("camvExecutor")
    @Provides
    SingleTaskExecutorService camvTaskExecutorService() {
        return new SingleTaskExecutorService();
    }

    @Named("foldingExecutor")
    @Provides
    SingleTaskExecutorService foldingTaskExecutorService() {
        return new SingleTaskExecutorService();
    }
}
