package io.gropp.pdfjumbler.actions;

import javax.swing.SwingWorker;

@FunctionalInterface
public interface WorkerRunner {
    boolean run(SwingWorker<?, ?> worker) throws Exception;
}
