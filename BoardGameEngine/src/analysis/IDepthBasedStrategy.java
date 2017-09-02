package analysis;

import java.util.List;

import analysis.search.MoveWithResult;
import game.IPosition;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public double evaluate(P position, int player, int plies);

	public void stopSearch();

	public double evaluateJoin(P position, int player, MoveWithResult<M> moveWithResult);

	public IDepthBasedStrategy<M, P> createCopy();

	public void notifySearchStarted();

	public void notifyForked(M parentMove, List<M> unanalyzedMoves);

	public void notifySearchComplete();
}
