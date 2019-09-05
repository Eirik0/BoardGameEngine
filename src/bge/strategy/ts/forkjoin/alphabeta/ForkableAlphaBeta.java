package bge.strategy.ts.forkjoin.alphabeta;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import bge.analysis.AnalysisResult;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkJoinNode;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.IForkable;
import bge.strategy.ts.forkjoin.IJoin;
import bge.strategy.ts.forkjoin.minmax.MinimaxForker;

public class ForkableAlphaBeta<M, P extends IPosition<M>> implements IForkable<M> {
    private final P position;
    private final MoveListFactory<M> moveListFactory;
    private final MoveList<M> movesToSearch;
    private final AtomicInteger branchIndex;
    private final int plies;

    private final IAlphaBetaPositionEvaluator<M, P> strategy;
    private final ForkableTreeSearchFactory<M, P> forkableFactory;

    private boolean searchCanceled = false;

    @SuppressWarnings("unchecked")
    public ForkableAlphaBeta(P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies,
            IAlphaBetaPositionEvaluator<M, P> strategy, ForkableTreeSearchFactory<M, P> forkableFactory) {
        this.position = (P) position.createCopy();
        this.movesToSearch = movesToSearch;
        branchIndex = new AtomicInteger(0);
        this.moveListFactory = moveListFactory;
        this.plies = plies;
        this.strategy = strategy;
        this.forkableFactory = forkableFactory;
    }

    @Override
    public AnalysisResult<M> search() {
        if (!isForkable()) {
            AnalysisResult<M> result = new AnalysisResult<>(position.getCurrentPlayer(), null, strategy.evaluate(position, plies));
            result.searchCompleted();
            return result;
        }
        int parentPlayer = position.getCurrentPlayer();
        double alpha = AnalysisResult.LOSS;
        double beta = AnalysisResult.WIN;
        AnalysisResult<M> analysisResult = new AnalysisResult<>(parentPlayer);
        do {
            M move = movesToSearch.get(branchIndex.get());
            position.makeMove(move);
            double score = parentPlayer == position.getCurrentPlayer() ? strategy.evaluate(position, plies - 1, alpha, beta)
                    : -strategy.evaluate(position, plies - 1, -beta, -alpha);
            position.unmakeMove(move);
            if (searchCanceled) { // we need to check search canceled after making the call to evaluate
                break;
            } else {
                analysisResult.addMoveWithScore(move, score);
                if (AnalysisResult.isGreater(score, alpha)) {
                    alpha = score;
                }
                if (!AnalysisResult.isGreater(beta, alpha)) { // alpha >= beta
                    branchIndex.set(movesToSearch.size()); // force search complete
                    break;
                }
            }
        } while (branchIndex.incrementAndGet() < movesToSearch.size());

        if (branchIndex.get() == movesToSearch.size()) {
            analysisResult.searchCompleted();
        }

        return analysisResult;
    }

    @Override
    public void stopSearch() {
        searchCanceled = true;
        strategy.stopSearch();
    }

    @Override
    public int getPlies() {
        return plies;
    }

    @Override
    public int getRemainingBranches() {
        return movesToSearch.size() - branchIndex.get();
    }

    @Override
    public List<ForkJoinNode<M>> fork(IJoin<M> parentJoin, M parentMove, AnalysisResult<M> currentPartial) {
        AnalysisResult<M> partialResult = currentPartial == null ? new AnalysisResult<>(position.getCurrentPlayer()) : currentPartial;
        MoveList<M> unanalyzedMoves = movesToSearch.subList(branchIndex.get());
        return MinimaxForker.fork(forkableFactory, position, unanalyzedMoves, moveListFactory, plies, parentJoin, parentMove, partialResult);
    }
}
