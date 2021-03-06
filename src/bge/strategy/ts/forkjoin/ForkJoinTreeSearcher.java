package bge.strategy.ts.forkjoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveWithScore;
import bge.analysis.PartialResultObservable;
import bge.analysis.StrategyResult;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.ITreeSearcher;
import gt.async.ThreadNumber;
import gt.async.ThreadWorker;

public class ForkJoinTreeSearcher<M, P extends IPosition<M>> implements ITreeSearcher<M, P>, PartialResultObservable {
    private Thread treeSearchThread;

    private final ForkableTreeSearchFactory<M, P> forkableFactory;
    private final MoveListFactory<M> moveListFactory;

    private final int numWorkers;

    private final List<ForkJoinNode<M>> nodesToAnalyze = new ArrayList<>();

    private final List<ThreadWorker> availableWorkers = new ArrayList<>();
    private final Map<ThreadWorker, ForkJoinNode<M>> treeSearchesInProgress = new HashMap<>();

    private final Object searchStartedLock = new Object();
    private volatile boolean searchStopped = true;
    private volatile boolean searchComplete = false;

    private int plies = 0;
    private volatile AnalysisResult<M> result;
    private ForkJoinRoot<M> treeSearchRoot = new ForkJoinRoot<>();

    public ForkJoinTreeSearcher(ForkableTreeSearchFactory<M, P> forkableFactory, MoveListFactory<M> moveListFactory, int numWorkers) {
        this.forkableFactory = forkableFactory;
        this.moveListFactory = moveListFactory;
        this.numWorkers = numWorkers;
        for (int i = 0; i < numWorkers; i++) {
            availableWorkers.add(new ThreadWorker(finishedWorker -> workerComplete(finishedWorker)));
        }
    }

    @Override
    public void searchForever(P position, boolean escapeEarly) {
        searchForever(position, Integer.MAX_VALUE, escapeEarly);
    }

    @SuppressWarnings("unchecked")
    public void searchForever(P position, int maxPlies, boolean escapeEarly) {
        searchStopped = true;
        searchComplete = false;
        treeSearchThread = new Thread(() -> startSearch((P) position.createCopy(), maxPlies, escapeEarly),
                "Tree_Search_Thread_" + ThreadNumber.getThreadNum(getClass()));
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

    public AnalysisResult<M> startSearch(P position, int maxPlies, boolean escapeEarly) {
        synchronized (searchStartedLock) {
            searchStopped = false;
            searchStartedLock.notify();
        }

        result = null;
        plies = 0;
        do {
            ++plies;
            AnalysisResult<M> search = search(position, plies);
            if (searchStopped && result != null) { // merge only when the search is stopped
                result = result.mergeWith(search);
            } else {
                // add back decided moves
                if (result != null) {
                    for (MoveWithScore<M> moveWithScore : result.getDecidedMoves()) {
                        search.addMoveWithScore(moveWithScore);
                    }
                }
                // return the previous result if the current is a loss for longevity
                if (escapeEarly && result != null && search.isLoss()) {
                    break;
                }
                result = search;
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

    @Override
    public boolean isSearching() {
        return !searchStopped;
    }

    @Override
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
            for (ThreadWorker worker : availableWorkers) {
                worker.joinThread();
            }
        }
    }

    @Override
    public AnalysisResult<M> getResult() {
        return result;
    }

    private synchronized void stopWorkers() { // Stopping a worker will eventually remove it from treeSearchesInProgress
        searchStopped = true;
        for (Entry<ThreadWorker, ForkJoinNode<M>> searchInProgress : treeSearchesInProgress.entrySet()) {
            searchInProgress.getKey().waitForStart();
            searchInProgress.getValue().stopSearch();
        }
    }

    public int getPlies() {
        return plies;
    }

    @Override
    public StrategyResult getPartialResult() {
        return new StrategyResult(result, treeSearchRoot.getPartialResult().getMovesWithScore(), plies);
    }

    private AnalysisResult<M> search(P position, int plies) {
        ResultTransfer<M> resultTransfer = new ResultTransfer<>();

        MoveList<M> searchMoveList = buildMoveList(position);

        IForkable<M> forkableSearch = forkableFactory.createNew(position, searchMoveList, moveListFactory, plies);
        ForkJoinNode<M> rootTreeSearch = new ForkJoinNode<>(null, forkableSearch,
                (canceled, moveWithResult) -> resultTransfer.putResult(moveWithResult.getSecond()));

        treeSearchRoot = new ForkJoinRoot<>(rootTreeSearch, position.getCurrentPlayer());
        nodesToAnalyze.addAll(treeSearchRoot.getBranches());

        int removeIndex = 0;
        while (availableWorkers.size() > nodesToAnalyze.size() && removeIndex < nodesToAnalyze.size() && !searchStopped) {
            ForkJoinNode<M> treeSearch = nodesToAnalyze.get(removeIndex);
            if (treeSearch.isForkable()) {
                nodesToAnalyze.addAll(nodesToAnalyze.remove(removeIndex).fork());
                removeIndex = 0;
            } else {
                ++removeIndex;
            }
        }

        if (searchStopped) {
            return new AnalysisResult<>(position.getCurrentPlayer());
        }

        synchronized (this) { // To prevent workers from completing before we have finished assigning work
            while (availableWorkers.size() > 0 && nodesToAnalyze.size() > 0) {
                startWork(availableWorkers.remove(0), nodesToAnalyze.remove(0));
            }
        }

        AnalysisResult<M> result = resultTransfer.awaitResult();
        waitForAvailableWorkers(); // All workers must become available before we return
        return result;
    }

    private MoveList<M> buildMoveList(IPosition<M> position) {
        MoveList<M> searchMoveList;
        if (result == null) {
            searchMoveList = moveListFactory.newAnalysisMoveList();
            position.getPossibleMoves(searchMoveList);
        } else {
            List<MoveWithScore<M>> undecidedMoves = new ArrayList<>();
            for (MoveWithScore<M> moveWithScore : result.getMovesWithScore()) {
                if (!AnalysisResult.isGameOver(moveWithScore.score)) {
                    undecidedMoves.add(moveWithScore);
                }
            }
            Collections.sort(undecidedMoves, (m1, m2) -> Double.compare(m2.score, m1.score));
            searchMoveList = moveListFactory.newAnalysisMoveList();
            for (MoveWithScore<M> entry : undecidedMoves) {
                searchMoveList.addQuietMove(entry.move, position);
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

    private void startWork(ThreadWorker worker, ForkJoinNode<M> treeSearch) {
        worker.workOn(treeSearch::search);
        treeSearchesInProgress.put(worker, treeSearch);
    }

    public synchronized void workerComplete(ThreadWorker finishedWorker) {
        if (nodesToAnalyze.size() > 0) {
            ForkJoinNode<M> node = nodesToAnalyze.remove(0);
            startWork(finishedWorker, node);
            if (searchStopped) {
                node.stopSearch();
            }
            return;
        }

        treeSearchesInProgress.remove(finishedWorker);

        Entry<ThreadWorker, ForkJoinNode<M>> treeSearchToFork = null;

        for (Entry<ThreadWorker, ForkJoinNode<M>> treeSearchInProgress : treeSearchesInProgress.entrySet()) {
            treeSearchInProgress.getKey().waitForStart();
            ForkJoinNode<M> treeSearch = treeSearchInProgress.getValue();
            if (!treeSearch.isForkable()) {
                continue;
            }
            if (treeSearchToFork == null
                    || treeSearch.getPlies() > treeSearchToFork.getValue().getPlies()
                    || (treeSearch.getPlies() == treeSearchToFork.getValue().getPlies()
                            && treeSearch.getRemainingBranches() > treeSearchToFork.getValue().getRemainingBranches())) {
                treeSearchToFork = treeSearchInProgress;
            }
        }

        if (!searchStopped && treeSearchToFork != null) {
            List<ForkJoinNode<M>> fork = treeSearchToFork.getValue().fork(); // Forking this worker will cause it to enqueue a call to workerComplete(this)
            treeSearchesInProgress.remove(treeSearchToFork.getKey()); // If we do not remove this from searches in progress, it can get reforked unnecessarily
            if (fork.size() > 0) {
                nodesToAnalyze.addAll(fork);
                startWork(finishedWorker, nodesToAnalyze.remove(0));
                return;
            }
        }

        availableWorkers.add(finishedWorker);
        notify();
    }
}
