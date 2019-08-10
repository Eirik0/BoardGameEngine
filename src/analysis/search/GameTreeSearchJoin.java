package analysis.search;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import analysis.AnalysisResult;
import analysis.AnalyzedMove;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;

public class GameTreeSearchJoin<M, P extends IPosition<M>> implements IGameTreeSearchJoin<M> {
    private final IGameTreeSearchJoin<M> parentJoin;
    private final M parentMove;
    private final P parentPosition;
    private final IDepthBasedStrategy<M, P> strategy;

    private final AnalysisResult<M> partialResult;
    private final int expectedResults;

    private final Map<M, AnalysisResult<M>> movesWithResults;
    private final AnalysisResult<M> remainderResult;

    private final AtomicBoolean parentAwaitingJoin = new AtomicBoolean(true);

    public GameTreeSearchJoin(IGameTreeSearchJoin<M> parentJoin, M parentMove, P parentPosition, IDepthBasedStrategy<M, P> strategy,
            AnalysisResult<M> partialResult,
            int expectedResults) {
        this.parentJoin = parentJoin;
        this.parentMove = parentMove;
        this.parentPosition = parentPosition;
        this.strategy = strategy;
        this.partialResult = partialResult;
        this.expectedResults = expectedResults;
        movesWithResults = new LinkedHashMap<>();
        remainderResult = new AnalysisResult<M>(partialResult.getPlayer()).mergeWith(partialResult);
    }

    @Override
    public synchronized void accept(boolean searchCanceled, MoveWithResult<M> moveWithResult) {
        movesWithResults.put(moveWithResult.move, moveWithResult.result);
        AnalyzedMove<M> bestMove = moveWithResult.result.getBestMove(remainderResult.getPlayer());
        if (bestMove != null) {
            remainderResult.addMoveWithScore(moveWithResult.move, bestMove.analysis);
        }
        if (searchCanceled || !moveWithResult.result.isSearchComplete()) {
            joinParent(searchCanceled);
        } else if (movesWithResults.size() == expectedResults) {
            partialResult.searchCompleted();
            joinParent(searchCanceled);
        }
    }

    private synchronized void joinParent(boolean searchCanceled) {
        if (parentAwaitingJoin.getAndSet(false)) {
            strategy.join(parentPosition, partialResult, movesWithResults);
            parentJoin.accept(searchCanceled, new MoveWithResult<>(parentMove, partialResult));
        }
    }

    @Override
    public synchronized AnalysisResult<M> getPartialResult() {
        return remainderResult;
    }

    @Override
    public IGameTreeSearchJoin<M> getParent() {
        return parentJoin;
    }
}
