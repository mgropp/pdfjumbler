package io.gropp.pdfjumbler.testutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;

public final class SynchronousWorkers {
    private SynchronousWorkers() {}

    public static boolean run(SwingWorker<?, ?> worker) throws Exception {
        CountDownLatch finished = new CountDownLatch(1);
        worker.addPropertyChangeListener(event -> {
            if (SwingWorker.StateValue.DONE.equals(event.getNewValue())) {
                finished.countDown();
            }
        });
        worker.execute();
        if (!finished.await(10, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Worker did not finish");
        }
        worker.get();
        return true;
    }
}
