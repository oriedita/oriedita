package oriedita.common.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiStagedExecutor implements Executor {

    private ExecutorService executor;

    public MultiStagedExecutor() {
        this.create();
    }

    public void create() {
        this.executor = Executors.newWorkStealingPool();
    }

    public void stopStage() throws InterruptedException {
        this.executor.shutdown();

        if (!this.executor.awaitTermination(10L, TimeUnit.SECONDS)) {
            throw new RuntimeException("Could not start");
        }
    }

    /**
     * Wait until every task is finished and start a new stage.
     */
    public void nextStage() throws InterruptedException {
        this.stopStage();

        this.executor = Executors.newWorkStealingPool();
    }

    @Override
    public void execute(Runnable command) {
        if (this.executor == null) {
            throw new IllegalStateException("MultiStageExecutor not started");
        }
        executor.execute(command);
    }
}
