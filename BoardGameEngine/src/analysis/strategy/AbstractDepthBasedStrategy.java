package analysis.strategy;

import java.util.List;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.search.MoveWithResult;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	protected static final int MAX_DEPTH = 64;

	protected final MoveListFactory<M> moveListFactory;
	@SuppressWarnings("unchecked")
	private final MoveList<M>[] moveLists = new MoveList[MAX_DEPTH];

	public AbstractDepthBasedStrategy(MoveListFactory<M> moveListFactory) {
		this.moveListFactory = moveListFactory;
	}

	protected volatile boolean searchCanceled = false;

	protected MoveList<M> getMoveList(int depth) {
		MoveList<M> moveList = moveLists[depth];
		if (moveList == null) {
			moveList = moveListFactory.newAnalysisMoveList();
			moveLists[depth] = moveList;
		}
		moveList.clear();
		return moveList;
	}

	@Override
	public void preSearch(AnalysisResult<M> currentResult, boolean isCurrentPlayer) {
		// do nothing by default
	}

	@Override
	public void stopSearch() {
		searchCanceled = true;
	}

	@Override
	public void notifyPlyStarted(AnalysisResult<M> lastResult) {
		// do nothing by default
	}

	@Override
	public void notifyForked(M parentMove, MoveList<M> unanalyzedMoves) {
		// do nothing by default
	}

	@Override
	public void join(P parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults) {
		for (MoveWithResult<M> moveWithResult : movesWithResults) {
			// Player and position come from the parent game tree search, so we are looking for the min for the current player
			MoveWithScore<M> moveWithScore = moveWithResult.result.getMax();
			if (moveWithScore == null) {
				continue;
			}
			partialResult.addMoveWithScore(moveWithResult.move, parentPlayer == currentPlayer ? moveWithScore.score : -moveWithScore.score, moveWithResult.result.isSeachComplete());
		}
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		// do nothing by default
	}
}
