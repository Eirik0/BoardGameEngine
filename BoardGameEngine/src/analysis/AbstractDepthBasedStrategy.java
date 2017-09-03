package analysis;

import java.util.List;

import game.IPosition;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	protected volatile boolean searchCanceled = false;

	@Override
	public void stopSearch() {
		searchCanceled = true;
	}

	@Override
	public void notifyPlyStarted() {
		// do nothing by default
	}

	@Override
	public void notifyForked(M parentMove, List<M> unanalyzedMoves) {
		// do nothing by default
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		// do nothing by default
	}
}
