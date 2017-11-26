package analysis.strategy;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	protected static final int MAX_DEPTH = 64;
	@SuppressWarnings("unchecked")
	private final MoveList<M>[] moveLists = new MoveList[MAX_DEPTH];

	protected volatile boolean searchCanceled = false;

	protected void initMoveLists(MoveListFactory<M> moveListFactory, int maxDepth) {
		int i = 0;
		while (i < maxDepth) {
			moveLists[i] = moveListFactory.newArrayMoveList();
			++i;
		}
	}

	protected MoveList<M> getMoveList(int depth) {
		MoveList<M> moveList = moveLists[depth - 1];
		moveList.clear();
		return moveList;
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
	public void notifyJoined(P parentPosition, M moves) {
		// do nothing by default
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		// do nothing by default
	}
}
