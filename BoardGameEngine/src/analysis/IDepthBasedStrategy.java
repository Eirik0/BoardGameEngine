package analysis;

import java.util.List;

import analysis.search.MoveWithResult;
import game.IPosition;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public double evaluate(P position, int player, int plies);

	public void stopSearch();

	public AnalysisResult<M> join(P position, int player, List<MoveWithScore<M>> movesWithScore, List<MoveWithResult<M>> movesWithResults);

	public IDepthBasedStrategy<M, P> createCopy();

	public void notifySearchStarted();

	public void notifyForked(M parentMove, List<M> unanalyzedMoves);

	public void notifySearchComplete();
}
