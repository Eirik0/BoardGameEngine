package analysis.strategy;

import java.util.Map;
import java.util.Map.Entry;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M>> implements IDepthBasedStrategy<M, P> {
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
	public void join(P parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults) {
		for (Entry<M, AnalysisResult<M>> moveWithResult : movesWithResults.entrySet()) {
			M move = moveWithResult.getKey();
			AnalysisResult<M> result = moveWithResult.getValue();
			MoveWithScore<M> moveWithScore = result.getMax();
			if (moveWithScore == null) {
				continue;
			}
			partialResult.addMoveWithScore(move, parentPlayer == currentPlayer ? moveWithScore.score : -moveWithScore.score, result.isSeachComplete());
		}
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		// do nothing by default
	}
}
