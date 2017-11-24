package analysis.strategy;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	protected volatile boolean searchCanceled = false;

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
