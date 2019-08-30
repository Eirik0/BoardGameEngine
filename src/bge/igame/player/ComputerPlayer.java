package bge.igame.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import bge.analysis.AnalysisResult;
import bge.analysis.ITreeSearcher;
import bge.analysis.PartialResultObservable;
import bge.igame.IPosition;

public class ComputerPlayer implements IPlayer {
    public static final String NAME = "Computer";

    private final ITreeSearcher<?, ?> treeSearcher;
    private final long msPerMove;
    private final boolean escapeEarly;

    public ComputerPlayer(ITreeSearcher<?, ?> treeSearcher, long msPerMove, boolean escapeEarly) {
        this.treeSearcher = treeSearcher;
        this.msPerMove = msPerMove;
        this.escapeEarly = escapeEarly;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <M> M getMove(IPosition<M> position) {
        long start = System.currentTimeMillis();
        ((ITreeSearcher<M, IPosition<M>>) treeSearcher).searchForever(position, escapeEarly);
        while (treeSearcher.isSearching() && msPerMove > System.currentTimeMillis() - start) {
            try {
                wait(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
    public void notifyTurnEnded() {
        treeSearcher.clearResult();
    }

    @Override
    public synchronized void notifyGameEnded() {
        stopSearch(true);
        notify();
    }

    public void stopSearch(boolean gameEnded) {
        treeSearcher.stopSearch(gameEnded);
    }

    @SuppressWarnings("unchecked")
    public ComputerPlayerResult getCurrentResult() {
        if (treeSearcher instanceof PartialResultObservable) {
            return ((PartialResultObservable) treeSearcher).getPartialResult();
        } else {
            return new ComputerPlayerResult((AnalysisResult<Object>) treeSearcher.getResult(), Collections.emptyMap(), 0);
        }
    }
}
