package analysis.search;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import game.IPosition;

public class TreeSearchWorker<M, P extends IPosition<M, P>> {
	private static int threadNum = 0;

	private final String threadName;

	private final Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer;

	private Thread thread;
	private volatile boolean notShutdown = true;
	private volatile boolean treeSearchSet = false;

	private final BlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<>(1);

	public TreeSearchWorker(Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer) {
		this("WorkerThread_" + threadNum++, completedWorkerConsumer);
	}

	public TreeSearchWorker(String threadName, Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer) {
		this.threadName = threadName;
		this.completedWorkerConsumer = completedWorkerConsumer;
	}

	private void maybeInitThread() {
		if (thread == null) {
			notShutdown = true;
			thread = new Thread(() -> {
				while (notShutdown) {
					try {
						runnableQueue.take().run();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}, threadName);

			thread.start();
		}
	}

	public void joinThread() {
		maybeInitThread();
		notShutdown = false;
		try {
			runnableQueue.put(() -> {
				// We have to say do nothing if we are waiting to take from the queue
			});
			thread.join();
			thread = null;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void workOn(GameTreeSearch<M, P> treeSearch) {
		treeSearchSet = false;
		maybeInitThread();
		try {
			runnableQueue.put(() -> {
				synchronized (this) {
					treeSearchSet = true;
					notify();
				}
				treeSearch.search();
				completedWorkerConsumer.accept(this);
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void waitForSearchToStart() {
		while (!treeSearchSet) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public int hashCode() {
		return threadName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		TreeSearchWorker<?, ?> other = (TreeSearchWorker<?, ?>) obj;
		return threadName.equals(other.threadName);
	}

	@Override
	public String toString() {
		return threadName;
	}
}
