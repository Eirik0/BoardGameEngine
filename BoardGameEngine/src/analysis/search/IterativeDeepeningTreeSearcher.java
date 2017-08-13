package analysis.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import game.IPosition;

public class IterativeDeepeningTreeSearcher<M, P extends IPosition<M, P>> {
	private Thread thread;

	private final IDepthBasedStrategy<M, P> strategy;

	private final int numWorkers;

	private final List<GameTreeSearch<M, P>> treeSearchesToAnalyze = new ArrayList<>();

	private final List<TreeSearchWorker<M, P>> availableWorkers = new ArrayList<>();
	private final List<TreeSearchWorker<M, P>> workingWorkers = new ArrayList<>();

	private volatile boolean searchNotStopped = false;

	private volatile int plies = 0;
	private volatile AnalysisResult<M> result;

	public IterativeDeepeningTreeSearcher(IDepthBasedStrategy<M, P> strategy, int numWorkers) {
		this.strategy = strategy;
		this.numWorkers = numWorkers;
		for (int i = 0; i < numWorkers; i++) {
			availableWorkers.add(new TreeSearchWorker<M, P>("WorkerThread_" + i, finishedWorker -> workerComplete(finishedWorker)));
		}
	}

	public void searchForever(P position) {
		result = null;
		thread = new Thread(() -> {
			searchNotStopped = true;
			plies = 0;
			do {
				++plies;
				AnalysisResult<M> search = search(position, position.getCurrentPlayer(), plies);
				result = result == null ? search : result.mergeWith(search);
			} while (searchNotStopped);
		});
		thread.start();
	}

	public void stopSearch() {
		stopWorkers();
		try {
			thread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void stopWorkers() { // Stopping a worker will eventually remove it from workingWorkers
		searchNotStopped = false;
		for (TreeSearchWorker<M, P> worker : workingWorkers) {
			worker.getTreeSearch().stopSearch();
		}
	}

	public int getPlies() {
		return plies;
	}

	public AnalysisResult<M> getResult() {
		return result;
	}

	public AnalysisResult<M> search(P position, int player, int plies) {
		BlockingQueue<AnalysisResult<M>> resultQueue = new SynchronousQueue<>();

		treeSearchesToAnalyze.add(new GameTreeSearch<>(null, position, player, plies, strategy, moveResult -> {
			try {
				resultQueue.put(moveResult.getSecond());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}));

		int removeIndex = 0;
		while (availableWorkers.size() > treeSearchesToAnalyze.size() && removeIndex < treeSearchesToAnalyze.size()) {
			if (treeSearchesToAnalyze.get(removeIndex).isForkable()) {
				GameTreeSearch<M, P> treeSearch = treeSearchesToAnalyze.remove(removeIndex);
				treeSearchesToAnalyze.addAll(treeSearch.fork());
			} else {
				++removeIndex;
			}
		}

		synchronized (this) { // To prevent workers from completing before we have finished assigning work
			while (availableWorkers.size() > 0 && treeSearchesToAnalyze.size() > 0) {
				TreeSearchWorker<M, P> worker = availableWorkers.remove(0);
				worker.workOn(treeSearchesToAnalyze.remove(0));
				workingWorkers.add(worker);
			}
		}

		AnalysisResult<M> result;
		try {
			result = resultQueue.take();
			synchronized (this) { // All workers must become available before we return
				while (availableWorkers.size() < numWorkers) {
					wait();
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public synchronized void workerComplete(TreeSearchWorker<M, P> finishedWorker) {
		if (treeSearchesToAnalyze.size() > 0) {
			finishedWorker.workOn(treeSearchesToAnalyze.remove(0));
			return;
		}
		workingWorkers.remove(finishedWorker);
		GameTreeSearch<M, P> toFork = null;
		for (TreeSearchWorker<M, P> worker : workingWorkers) {
			GameTreeSearch<M, P> treeSearch = worker.getTreeSearch();
			if (toFork == null || treeSearch.getPlies() > toFork.getPlies() || (treeSearch.getPlies() == toFork.getPlies() && treeSearch.getRemainingBranches() > toFork.getRemainingBranches())) {
				if (treeSearch.isForkable()) {
					toFork = treeSearch;
				}
			}
		}
		if (searchNotStopped && toFork != null) {
			List<GameTreeSearch<M, P>> fork = toFork.fork(); // Forking this worker will cause it to enqueue a call to workerComplete(this)
			if (fork.size() > 0) {
				treeSearchesToAnalyze.addAll(fork);
				finishedWorker.workOn(treeSearchesToAnalyze.remove(0));
				workingWorkers.add(finishedWorker);
				return;
			}
		}
		availableWorkers.add(finishedWorker);
		notify();
	}
}
