package analysis.strategy;

import analysis.AnalysisResult;
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
