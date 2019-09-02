package bge.strategy;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import bge.analysis.AnalysisResult;
import bge.analysis.ITreeSearcher;
import bge.analysis.PartialResultObservable;
import bge.igame.IPosition;
import bge.igame.player.StrategyResult;

public class TreeSearchStrategy<M> implements IStrategy<M>, InterruptableStrategy, ObservableStrategy {
    private final ITreeSearcher<?, ?> treeSearcher;
    private final long msPerMove;
    private final boolean escapeEarly;

    public TreeSearchStrategy(ITreeSearcher<?, ?> treeSearcher, long msPerMove, boolean escapeEarly) {
        this.treeSearcher = treeSearcher;
        this.msPerMove = msPerMove;
        this.escapeEarly = escapeEarly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public M getMove(IPosition<M> position) {
        // TODO clear result ?
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

        if (treeSearcher.isSearching()) {
            treeSearcher.stopSearch(false);
        }

        AnalysisResult<M> result = (AnalysisResult<M>) treeSearcher.getResult();

        List<M> bestMoves = result == null ? Collections.emptyList() : result.getBestMoves();

        return bestMoves.size() > 0 ? bestMoves.get(new Random().nextInt(bestMoves.size())) : null;
    }

    @Override
    public synchronized void pauseSearch() {
        treeSearcher.stopSearch(false);
        notify();
    }

    @Override
    public synchronized void stopSearch() {
        treeSearcher.stopSearch(true);
        notify();
    }

    @SuppressWarnings("unchecked")
    @Override
    public StrategyResult getCurrentResult() {
        if (treeSearcher instanceof PartialResultObservable) {
            return ((PartialResultObservable) treeSearcher).getPartialResult();
        } else {
            return new StrategyResult((AnalysisResult<Object>) treeSearcher.getResult(), Collections.emptyMap(), 0);
        }
    }
}
