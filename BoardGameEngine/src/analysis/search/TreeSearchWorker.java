package analysis.search;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import game.IPosition;

public class TreeSearchWorker<M, P extends IPosition<M, P>> {
	private static int threadNum = 0;

	private final String threadName;

	private final Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer;

	private final Thread thread;
	private volatile boolean notShutdown = true;
	private volatile boolean treeSearchNotSet = true;

	private final BlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<>(1);
	private volatile GameTreeSearch<M, P> treeSearch;

	public TreeSearchWorker(Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer) {
		this("WorkerThread_" + threadNum++, completedWorkerConsumer);
	}

	public TreeSearchWorker(String threadName, Consumer<TreeSearchWorker<M, P>> completedWorkerConsumer) {
		this.threadName = threadName;
		this.completedWorkerConsumer = completedWorkerConsumer;

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

	public void joinThread() {
		notShutdown = false;
		try {
			runnableQueue.put(() -> {
				// We have to say do nothing if we are waiting to take from the queue
			});
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void workOn(GameTreeSearch<M, P> treeSearch) {
		treeSearchNotSet = true;
		try {
			runnableQueue.put(() -> {
				synchronized (this) {
					this.treeSearch = treeSearch;
					treeSearchNotSet = false;
					notify();
				}
				treeSearch.search();
				completedWorkerConsumer.accept(this);
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized GameTreeSearch<M, P> getTreeSearch() { // XXX is there a better way to do this?
		while (treeSearchNotSet) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return treeSearch;
	}

	@Override
	public String toString() {
		return threadName;
	}
}
