package analysis;

import java.util.List;

import game.IPosition;
import util.Pair;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public double evaluate(P position, int player, int plies);

	public void stopSearch();

	public AnalysisResult<M> join(P position, int player, List<MoveWithScore<M>> movesWithScore, List<Pair<M, AnalysisResult<M>>> results);

	public IDepthBasedStrategy<M, P> createCopy();

	public void notifySearchStarted();

	public void notifyForked(M parentMove, List<M> unanalyzedMoves);

	public void notifySearchComplete();
}
