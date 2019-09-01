package bge.analysis.search;

import java.util.concurrent.atomic.AtomicReference;

import bge.analysis.AnalysisResult;

public class ResultTransfer<M> {
    private final AtomicReference<AnalysisResult<M>> move = new AtomicReference<>(null);

    public synchronized void putResult(AnalysisResult<M> result) {
        if (move.get() != null) {
            return;
        }
        move.set(result);
        notify();
    }

    public AnalysisResult<M> awaitResult() {
        AnalysisResult<M> result;
        while ((result = move.get()) == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return result;
    }
}
