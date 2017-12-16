package analysis.search;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import analysis.AnalysisResult;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;

public class GameTreeSearchJoin<M, P extends IPosition<M, P>> implements IGameTreeSearchJoin<M, P> {
	private final IGameTreeSearchJoin<M, P> parentJoin;
	private final M parentMove;
	private final P parentPosition;
	private final int parentPlayer;
	private final IDepthBasedStrategy<M, P> strategy;

	private final AnalysisResult<M> partialResult;
	private final int expectedResults;

	private final Map<M, AnalysisResult<M>> movesWithResults;

	private final AtomicBoolean parentAwaitingJoin = new AtomicBoolean(true);

	public GameTreeSearchJoin(IGameTreeSearchJoin<M, P> parentJoin, M parentMove, P parentPosition, int parentPlayer, IDepthBasedStrategy<M, P> strategy, AnalysisResult<M> partialResult,
			int expectedResults) {
		this.parentJoin = parentJoin;
		this.parentMove = parentMove;
		this.parentPosition = parentPosition;
		this.parentPlayer = parentPlayer;
		this.strategy = strategy;
		this.partialResult = partialResult;
		this.expectedResults = expectedResults;
		movesWithResults = new LinkedHashMap<>();
	}

	@Override
	public synchronized void accept(boolean searchCanceled, int currentPlayer, MoveWithResult<M> moveWithResult) {
		movesWithResults.put(moveWithResult.move, moveWithResult.result);
		if (searchCanceled) {
			joinParent(searchCanceled, currentPlayer);
		} else if (!moveWithResult.result.isSeachComplete()) {
			joinParent(searchCanceled, currentPlayer);
		} else if (movesWithResults.size() == expectedResults) {
			partialResult.searchCompleted();
			joinParent(searchCanceled, currentPlayer);
		}
	}

	private synchronized void joinParent(boolean searchCanceled, int currentPlayer) {
		if (parentAwaitingJoin.getAndSet(false)) {
			strategy.join(parentPosition, parentPlayer, currentPlayer, partialResult, movesWithResults);
			parentJoin.accept(searchCanceled, parentPlayer, new MoveWithResult<>(parentMove, partialResult));
		}
	}
}
