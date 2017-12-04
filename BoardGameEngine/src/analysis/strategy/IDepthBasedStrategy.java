package analysis.strategy;

import java.util.List;

import analysis.AnalysisResult;
import analysis.search.MoveWithResult;
import game.IPosition;
import game.MoveList;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public void preSearch(AnalysisResult<M> currentResult, boolean isCurrentPlayer);

	public double evaluate(P position, int plies);

	public void stopSearch();

	public void notifyPlyStarted(AnalysisResult<M> lastResult);

	public void notifyForked(M parentMove, MoveList<M> unanalyzedMoves);

	public void join(P parentPosition, int rootPlayer, int currentPlayer, AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults);

	public void notifyPlyComplete(boolean searchStopped);

	public IDepthBasedStrategy<M, P> createCopy();
}
