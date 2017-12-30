package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import analysis.AnalysisResult;
import analysis.MoveAnalysis;
import analysis.strategy.ForkJoinObserver;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.IForkable;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class IterativeDeepeningTreeSearcher<M, P extends IPosition<M>> {
	private Thread treeSearchThread;

	private final IDepthBasedStrategy<M, P> strategy;
	private final MoveListFactory<M> moveListFactory;

	private final int numWorkers;

	private final List<GameTreeSearch<M, P>> treeSearchesToAnalyze = new ArrayList<>();

	private final List<TreeSearchWorker> availableWorkers = new ArrayList<>();
	private final Map<TreeSearchWorker, GameTreeSearch<M, P>> treeSearchesInProgress = new HashMap<>();

	private final Object searchStartedLock = new Object();
	private volatile boolean searchStopped = true;
	private volatile boolean searchComplete = false;

	private int plies = 0;
	private volatile AnalysisResult<M> result;
	private TreeSearchRoot<M, P> treeSearchRoot = new TreeSearchRoot<>();

	public IterativeDeepeningTreeSearcher(IDepthBasedStrategy<M, P> strategy, MoveListFactory<M> moveListFactory, int numWorkers) {
		this.strategy = strategy;
		this.moveListFactory = moveListFactory;
		this.numWorkers = numWorkers;
		for (int i = 0; i < numWorkers; i++) {
			availableWorkers.add(new TreeSearchWorker(finishedWorker -> workerComplete(finishedWorker)));
		}
	}

	public void searchForever(P position, boolean escapeEarly) {
		searchForever(position, Integer.MAX_VALUE, escapeEarly);
	}

	@SuppressWarnings("unchecked")
	public void searchForever(P position, int maxPlies, boolean escapeEarly) {
		searchStopped = true;
		searchComplete = false;
		treeSearchThread = new Thread(() -> startSearch((P) position.createCopy(), maxPlies, escapeEarly), "Tree_Search_Thread_" + ThreadNumber.getThreadNum(getClass()));
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

	@SuppressWarnings("unchecked")
	public AnalysisResult<M> startSearch(P position, int maxPlies, boolean escapeEarly) {
		synchronized (searchStartedLock) {
			searchStopped = false;
			searchStartedLock.notify();
		}

		result = null;
		plies = 0;
		do {
			++plies;
			if (strategy instanceof ForkJoinObserver<?>) {
				((ForkJoinObserver<M>) strategy).notifyPlyStarted(result);
			}
			AnalysisResult<M> search = search(position, plies);
			if (searchStopped && result != null) { // merge only when the search is stopped
				result = result.mergeWith(search);
			} else {
				// add back decided moves
				if (result != null) {
					for (Entry<M, MoveAnalysis> moveWithScore : result.getDecidedMoves().entrySet()) {
						search.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
					}
				}
				// return the previous result if the current is a loss for longevity
				if (escapeEarly && result != null && search.isLoss()) {
					break;
				}
				result = search;
			}
			if (strategy instanceof ForkJoinObserver<?>) {
				((ForkJoinObserver<M>) strategy).notifyPlyComplete(searchStopped);
			}
			if (escapeEarly && (result.isWin() || result.onlyOneMove()) || result.isDecided()) {
				break; // when escaping early, break if the game is won, or there is only one move; or if all moves are decided
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

	public void clearResult() {
		result = null;
		treeSearchRoot = new TreeSearchRoot<>();
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
			for (TreeSearchWorker worker : availableWorkers) {
				worker.joinThread();
			}
		}
	}

	private synchronized void stopWorkers() { // Stopping a worker will eventually remove it from treeSearchesInProgress
		searchStopped = true;
		for (Entry<TreeSearchWorker, GameTreeSearch<M, P>> searchInProgress : treeSearchesInProgress.entrySet()) {
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

	public Map<M, MoveAnalysis> getPartialResult() {
		return treeSearchRoot.getPartialResult().getMovesWithScore();
	}

	private AnalysisResult<M> search(P position, int plies) {
		ResultTransfer<M> resultTransfer = new ResultTransfer<>();

		MoveList<M> searchMoveList = buildMoveList(position);

		IForkable<M, P> forkableSearch = strategy.newForkableSearch(null, position, searchMoveList, moveListFactory, plies);
		GameTreeSearch<M, P> rootTreeSearch = new GameTreeSearch<>(forkableSearch, (canceled, moveWithResult) -> resultTransfer.putResult(moveWithResult.result));

		treeSearchRoot = new TreeSearchRoot<>(rootTreeSearch, position.getCurrentPlayer());
		treeSearchesToAnalyze.addAll(treeSearchRoot.getBranches());

		int removeIndex = 0;
		while (availableWorkers.size() > treeSearchesToAnalyze.size() && removeIndex < treeSearchesToAnalyze.size() && !searchStopped) {
			GameTreeSearch<M, P> treeSearch = treeSearchesToAnalyze.get(removeIndex);
			if (treeSearch.isForkable()) {
				treeSearchesToAnalyze.addAll(treeSearchesToAnalyze.remove(removeIndex).fork());
				removeIndex = 0;
			} else {
				++removeIndex;
			}
		}

		if (searchStopped) {
			return new AnalysisResult<>(position.getCurrentPlayer());
		}

		synchronized (this) { // To prevent workers from completing before we have finished assigning work
			while (availableWorkers.size() > 0 && treeSearchesToAnalyze.size() > 0) {
				startWork(availableWorkers.remove(0), treeSearchesToAnalyze.remove(0));
			}
		}

		AnalysisResult<M> result = resultTransfer.awaitResult();
		waitForAvailableWorkers(); // All workers must become available before we return
		return result;
	}

	private MoveList<M> buildMoveList(P position) {
		MoveList<M> searchMoveList;
		if (result == null) {
			searchMoveList = moveListFactory.newAnalysisMoveList();
			position.getPossibleMoves(searchMoveList);
		} else {
			List<Entry<M, MoveAnalysis>> undecidedMoves = new ArrayList<>();
			for (Entry<M, MoveAnalysis> moveWithScore : result.getMovesWithScore().entrySet()) {
				if (!AnalysisResult.isGameOver(moveWithScore.getValue().score)) {
					undecidedMoves.add(moveWithScore);
				}
			}
			Collections.sort(undecidedMoves, (m1, m2) -> Double.compare(m2.getValue().score, m1.getValue().score));
			searchMoveList = moveListFactory.newArrayMoveList();
			for (Entry<M, MoveAnalysis> entry : undecidedMoves) {
				searchMoveList.addQuietMove(entry.getKey(), position);
			}
		}
		return searchMoveList;
	}

	private synchronized void waitForAvailableWorkers() {
		while (availableWorkers.size() < numWorkers) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void startWork(TreeSearchWorker worker, GameTreeSearch<M, P> treeSearch) {
		worker.workOn(treeSearch);
		treeSearchesInProgress.put(worker, treeSearch);
	}

	public synchronized void workerComplete(TreeSearchWorker finishedWorker) {
		if (treeSearchesToAnalyze.size() > 0) {
			GameTreeSearch<M, P> treeSearch = treeSearchesToAnalyze.remove(0);
			startWork(finishedWorker, treeSearch);
			if (searchStopped) {
				treeSearch.stopSearch();
			}
			return;
		}

		treeSearchesInProgress.remove(finishedWorker);

		Entry<TreeSearchWorker, GameTreeSearch<M, P>> treeSearchToFork = null;

		for (Entry<TreeSearchWorker, GameTreeSearch<M, P>> treeSearchInProgress : treeSearchesInProgress.entrySet()) {
			treeSearchInProgress.getKey().waitForSearchToStart();
			GameTreeSearch<M, P> treeSearch = treeSearchInProgress.getValue();
			if (!treeSearch.isForkable()) {
				continue;
			}
			if (treeSearchToFork == null
					|| treeSearch.getPlies() > treeSearchToFork.getValue().getPlies()
					|| (treeSearch.getPlies() == treeSearchToFork.getValue().getPlies() && treeSearch.getRemainingBranches() > treeSearchToFork.getValue().getRemainingBranches())) {
				treeSearchToFork = treeSearchInProgress;
			}
		}

		if (!searchStopped && treeSearchToFork != null) {
			List<GameTreeSearch<M, P>> fork = treeSearchToFork.getValue().fork(); // Forking this worker will cause it to enqueue a call to workerComplete(this)
			treeSearchesInProgress.remove(treeSearchToFork.getKey()); // If we do not remove this from searches in progress, it can get reforked unnecessarily
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
