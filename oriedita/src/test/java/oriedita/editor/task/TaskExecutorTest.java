package oriedita.editor.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import oriedita.editor.service.TaskExecutorService;
import oriedita.editor.service.impl.SingleTaskExecutorServiceImpl;

import java.util.concurrent.CountDownLatch;

public class TaskExecutorTest {
    @Test
    public void testTaskExecutorSuccess() throws Exception {
        TaskExecutorService executorService = new SingleTaskExecutorServiceImpl();

        CountDownLatch latch1 = new CountDownLatch(1);

        TestTask task1 = new TestTask("Task1", latch1);
        executorService.executeTask(task1);

        Assertions.assertEquals("Task1", executorService.getTaskName());
        Assertions.assertTrue(executorService.isTaskRunning());

        latch1.countDown();

        executorService.join();

        Assertions.assertFalse(executorService.isTaskRunning());
        Assertions.assertEquals(TestTask.State.FINISHED, task1.state);
    }

    @Test
    public void testTaskExecutor() throws Exception {
        TaskExecutorService executorService = new SingleTaskExecutorServiceImpl();

        CountDownLatch latch1 = new CountDownLatch(1);

        TestTask task1 = new TestTask("Task1", latch1);
        executorService.executeTask(task1);

        Assertions.assertEquals("Task1", executorService.getTaskName());
        Assertions.assertTrue(executorService.isTaskRunning());

        CountDownLatch latch2 = new CountDownLatch(1);
        TestTask task2 = new TestTask("Task2", latch2);

        Assertions.assertEquals(TestTask.State.CREATED, task2.state);

        executorService.executeTask(task2);

        Assertions.assertEquals("Task2", executorService.getTaskName());
        Assertions.assertTrue(executorService.isTaskRunning());

        latch2.countDown();

        executorService.join();

        Assertions.assertFalse(executorService.isTaskRunning());
        Assertions.assertNotEquals(TestTask.State.FINISHED, task1.state);
        Assertions.assertEquals(TestTask.State.FINISHED, task2.state);
    }

    /**
     * A test task, uses a CountDownLatch to trigger finishing this task.
     */
    private static class TestTask implements OrieditaTask {
        private final String name;
        private final CountDownLatch latch;

        private State state;

        private enum State {
            CREATED, RUNNING, CANCELLED, FINISHED,
        }

        public TestTask(String name, CountDownLatch latch) {
            this.name = name;
            this.latch = latch;

            state = State.CREATED;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void run() {
            state = State.RUNNING;
            try {
                latch.await();
            } catch (InterruptedException e) {
                state = State.CANCELLED;
                throw new RuntimeException(e);
            }

            state = State.FINISHED;
        }
    }
}
