package analysis.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import analysis.AnalysisResult;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;

public class IterativeDeepeningTreeSearcher<M, P extends IPosition<M, P>> {
	private Thread treeSearchThread;

	private final IDepthBasedStrategy<M, P> strategy;

	private final int numWorkers;

	private final List<GameTreeSearch<M, P>> treeSearchesToAnalyze = new ArrayList<>();

	private final List<TreeSearchWorker<M, P>> availableWorkers = new ArrayList<>();
	private final Map<TreeSearchWorker<M, P>, GameTreeSearch<M, P>> treeSearchesInProgress = new HashMap<>();

	private final Object searchStartedLock = new Object();
	private volatile boolean searchStopped = true;
	private volatile boolean searchComplete = false;

	private int plies = 0;
	private volatile AnalysisResult<M> result;

	public IterativeDeepeningTreeSearcher(IDepthBasedStrategy<M, P> strategy, int numWorkers) {
		this.strategy = strategy;
		this.numWorkers = numWorkers;
		for (int i = 0; i < numWorkers; i++) {
			availableWorkers.add(new TreeSearchWorker<M, P>(finishedWorker -> workerComplete(finishedWorker)));
		}
	}

	public void searchForever(P position) {
		searchForever(position, Integer.MAX_VALUE);
	}

	public void searchForever(P position, int maxPlies) {
		searchStopped = true;
		searchComplete = false;
		treeSearchThread = new Thread(() -> startSearch(position, maxPlies), "Tree_Search_Thread_" + ThreadNumber.getThreadNum(getClass()));
		treeSearchThread.start();
		synchronized (searchStartedLock) {
			while (searchStopped && !searchComplete) {
				try {
					searchStartedLock.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	public AnalysisResult<M> startSearch(P position, int maxPlies) {
		synchronized (searchStartedLock) {
			searchStopped = false;
			searchStartedLock.notify();
		}

		result = null;
		plies = 0;
		do {
			++plies;
			strategy.notifyPlyStarted(result);
			AnalysisResult<M> search = search(position, position.getCurrentPlayer(), plies);
			if (search.searchedAllPositions()) {
				result.setSearchedAllPositions(true);
				break; // if we have searched all positions, the last result was complete
			}
			if (searchStopped && result != null) { // merge only when the search is stopped
				result.mergeWith(search);
			} else {
				result = search;
			}
			strategy.notifyPlyComplete(searchStopped);
			if (result.getBestMove() != null && Double.isInfinite(result.getMax())) {
				break; // no need to keep looking if the game is decided
			}
		} while (!searchStopped && plies < maxPlies);

		synchronized (searchStartedLock) {
			searchStopped = true;
			searchComplete = true; // if we reset searchStopped we need to make sure the lock does not wait forever
			searchStartedLock.notify();
		}
		return result;
	}

	public boolean isSearching() {
		return !searchStopped;
	}

	public void stopSearch(boolean joinWorkerThreads) {
		stopWorkers();
		try {
			if (treeSearchThread != null) {
				treeSearchThread.join();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (joinWorkerThreads) {
			for (TreeSearchWorker<M, P> worker : availableWorkers) {
				worker.joinThread();
			}
		}
	}

	private synchronized void stopWorkers() { // Stopping a worker will eventually remove it from workingWorkers
		searchStopped = true;
		for (Entry<TreeSearchWorker<M, P>, GameTreeSearch<M, P>> searchInProgress : treeSearchesInProgress.entrySet()) {
			searchInProgress.getKey().waitForSearchToStart();
			searchInProgress.getValue().stopSearch();
		}
	}

	public int getPlies() {
		return plies;
	}

	public AnalysisResult<M> getResult() {
		return result;
	}

	private AnalysisResult<M> search(P position, int player, int plies) {
		BlockingQueue<AnalysisResult<M>> resultQueue = new SynchronousQueue<>();

		treeSearchesToAnalyze.add(new GameTreeSearch<>(null, position, player, plies, strategy, moveResult -> {
			try {
				resultQueue.put(moveResult.result);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}));

		int removeIndex = 0;
		while (availableWorkers.size() > treeSearchesToAnalyze.size() && removeIndex < treeSearchesToAnalyze.size()) {
			GameTreeSearch<M, P> treeSearch = treeSearchesToAnalyze.get(removeIndex);
			if (treeSearch.getPlies() > 0 && treeSearch.getRemainingBranches() > 0) {
				treeSearchesToAnalyze.addAll(treeSearchesToAnalyze.remove(removeIndex).fork());
				removeIndex = 0;
			} else {
				++removeIndex;
			}
		}

		synchronized (this) { // To prevent workers from completing before we have finished assigning work
			while (availableWorkers.size() > 0 && treeSearchesToAnalyze.size() > 0) {
				startWork(availableWorkers.remove(0), treeSearchesToAnalyze.remove(0));
			}
		}

		try {
			AnalysisResult<M> result = resultQueue.take();
			synchronized (this) { // All workers must become available before we return
				while (availableWorkers.size() < numWorkers) {
					wait();
				}
			}
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void startWork(TreeSearchWorker<M, P> worker, GameTreeSearch<M, P> treeSearch) {
		worker.workOn(treeSearch);
		treeSearchesInProgress.put(worker, treeSearch);
	}

	public synchronized void workerComplete(TreeSearchWorker<M, P> finishedWorker) {
		if (treeSearchesToAnalyze.size() > 0) {
			GameTreeSearch<M, P> treeSearch = treeSearchesToAnalyze.remove(0);
			startWork(finishedWorker, treeSearch);
			if (searchStopped) {
				treeSearch.stopSearch();
			}
			return;
		}

		treeSearchesInProgress.remove(finishedWorker);

		GameTreeSearch<M, P> toFork = null;
		for (Entry<TreeSearchWorker<M, P>, GameTreeSearch<M, P>> treeSearchInProgress : treeSearchesInProgress.entrySet()) {
			treeSearchInProgress.getKey().waitForSearchToStart();
			GameTreeSearch<M, P> treeSearch = treeSearchInProgress.getValue();
			if (treeSearch.getPlies() > 0) {
				if (toFork == null
						|| treeSearch.getPlies() > toFork.getPlies()
						|| (treeSearch.getPlies() == toFork.getPlies() && treeSearch.getRemainingBranches() >= toFork.getRemainingBranches())) {
					if (treeSearch.getRemainingBranches() > 0) {
						toFork = treeSearch;
					}
				}
			}
		}

		if (!searchStopped && toFork != null) {
			List<GameTreeSearch<M, P>> fork = toFork.fork(); // Forking this worker will cause it to enqueue a call to workerComplete(this)
			if (fork.size() > 0) {
				treeSearchesToAnalyze.addAll(fork);
				startWork(finishedWorker, treeSearchesToAnalyze.remove(0));
				return;
			}
		}

		availableWorkers.add(finishedWorker);
		notify();
	}
}
