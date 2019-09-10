package bge.strategy.ts;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import bge.analysis.AnalysisResult;
import bge.analysis.PartialResultObservable;
import bge.analysis.StrategyResult;
import bge.igame.IPosition;
import bge.strategy.IStrategy;
import bge.strategy.InterruptableStrategy;
import bge.strategy.ObservableStrategy;

public class TreeSearchStrategy<M> implements IStrategy<M>, InterruptableStrategy, ObservableStrategy {
    private final ITreeSearcher<?, ?> treeSearcher;
    private final long msPerMove;
    private final boolean escapeEarly;

    private volatile boolean stopRequested = false;

    public TreeSearchStrategy(ITreeSearcher<?, ?> treeSearcher, long msPerMove, boolean escapeEarly) {
        this.treeSearcher = treeSearcher;
        this.msPerMove = msPerMove;
        this.escapeEarly = escapeEarly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getMove(IPosition<M> position) {
        stopRequested = false;
        long start = System.currentTimeMillis();
        ((ITreeSearcher<M, IPosition<M>>) treeSearcher).searchForever(position, escapeEarly);
        synchronized (this) {
            while (treeSearcher.isSearching() && msPerMove > System.currentTimeMillis() - start) {
                try {
                    wait(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (!stopRequested) {
            treeSearcher.stopSearch(false);
        }

        AnalysisResult<M> result = (AnalysisResult<M>) treeSearcher.getResult();

        List<M> bestMoves = result == null ? Collections.emptyList() : result.getBestMoves();

        return bestMoves.size() > 0 ? bestMoves.get(new Random().nextInt(bestMoves.size())) : null;
    }

    @Override
    public synchronized void pauseSearch() {
        stopRequested = true;
        treeSearcher.stopSearch(false);
        notify();
    }

    @Override
    public synchronized void stopSearch() {
        stopRequested = true;
        treeSearcher.stopSearch(true);
        notify();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StrategyResult getCurrentResult() {
        if (treeSearcher instanceof PartialResultObservable) {
            return ((PartialResultObservable) treeSearcher).getPartialResult();
        } else {
            return new StrategyResult((AnalysisResult<Object>) treeSearcher.getResult(), Collections.emptyList(), 0);
        }
    }
}
