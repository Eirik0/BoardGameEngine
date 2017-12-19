package analysis.strategy;

import java.util.Map;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;

public interface IDepthBasedStrategy<M, P extends IPosition<M>> {
	public void preSearch(AnalysisResult<M> currentResult, boolean isCurrentPlayer);

	public double evaluate(P position, int plies);

	public void stopSearch();

	public void notifyPlyStarted(AnalysisResult<M> lastResult);

	public void notifyForked(M parentMove, MoveList<M> unanalyzedMoves);

	public void join(P parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults);

	public void notifyPlyComplete(boolean searchStopped);

	public IDepthBasedStrategy<M, P> createCopy();
}
