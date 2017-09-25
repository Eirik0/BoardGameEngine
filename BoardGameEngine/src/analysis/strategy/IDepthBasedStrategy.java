package analysis.strategy;

import java.util.List;

import analysis.AnalysisResult;
import game.IPosition;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public double evaluate(P position, int player, int plies);

	public void stopSearch();

	public boolean searchedAllPositions();

	public void notifyPlyStarted(AnalysisResult<M> lastResult);

	public void notifyForked(M parentMove, List<M> unanalyzedMoves);

	public void notifyJoined(P parentPosition, M moves);

	public void notifyPlyComplete(boolean searchStopped);

	public IDepthBasedStrategy<M, P> createCopy();
}
