package origami_editor.editor.task;

import java.util.concurrent.*;

/**
 * A future which has already finished. Used as a placeholder in cases where a future is optional, but decisions are made on if the future is done.
 * @param <T>
 */
public class FinishedFuture<T> implements Future<T> {
    T value;

    public FinishedFuture(T value) {
        this.value = value;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return value;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return value;
    }
}
