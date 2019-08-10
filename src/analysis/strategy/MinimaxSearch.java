package analysis.strategy;

import analysis.AnalysisResult;
import analysis.MoveAnalysis;
import analysis.search.IGameTreeSearchJoin;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class MinimaxSearch<M, P extends IPosition<M>> extends AbstractAlphaBetaSearch<M, P> {
    public MinimaxSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies,
            IDepthBasedStrategy<M, P> strategy) {
        super(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
    }

    @Override
    protected MinimaxSearch<M, P> newSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies,
            IDepthBasedStrategy<M, P> strategy) {
        return new MinimaxSearch<>(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
    }

    @Override
    protected synchronized AnalysisResult<M> searchNonForkable(IGameTreeSearchJoin<M> join) {
        AnalysisResult<M> result = new AnalysisResult<>(position.getCurrentPlayer(), parentMove, strategy.evaluate(position, plies));
        result.searchCompleted();
        return result;
    }

    @Override
    protected synchronized AnalysisResult<M> searchWithStrategy(IGameTreeSearchJoin<M> join) {
        int parentPlayer = position.getCurrentPlayer();
        AnalysisResult<M> analysisResult = new AnalysisResult<>(parentPlayer);
        do {
            M move = movesToSearch.get(branchIndex.get());
            position.makeMove(move);
            double evaluate = strategy.evaluate(position, plies - 1);
            double score = searchCanceled ? 0 : parentPlayer == position.getCurrentPlayer() ? evaluate : -evaluate;
            position.unmakeMove(move);
            if (searchCanceled) { // we need to check search canceled after making the call to evaluate
                break;
            } else {
                analysisResult.addMoveWithScore(move, new MoveAnalysis(score));
            }
        } while (branchIndex.incrementAndGet() < movesToSearch.size());

        if (branchIndex.get() == movesToSearch.size()) {
            analysisResult.searchCompleted();
        }

        return analysisResult;
    }
}
