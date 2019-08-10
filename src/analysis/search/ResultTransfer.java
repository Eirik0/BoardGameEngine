package analysis.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import analysis.AnalysisResult;

public class ResultTransfer<M> {
	private final BlockingQueue<AnalysisResult<M>> resultQueue = new SynchronousQueue<>();
	private final AtomicBoolean resultPut = new AtomicBoolean(false);
	private final AtomicBoolean awaitingResult = new AtomicBoolean(true);

	public synchronized void putResult(AnalysisResult<M> result) {
		if (resultPut.getAndSet(true)) { // synchronized and an atomic boolean is not enough, which is why we wait
			return;
		}
		try {
			resultQueue.put(result);
			while (awaitingResult.get()) {
				wait();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public AnalysisResult<M> awaitResult() {
		try {
			AnalysisResult<M> result = resultQueue.take();
			synchronized (this) {
				awaitingResult.set(false);
				notify();
			}
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
