package analysis.strategy;

import java.util.Map;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public interface IDepthBasedStrategy<M, P extends IPosition<M>> {
	public default IForkable<M, P> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies) {
		return newForkableSearch(parentMove, position, movesToSearch, moveListFactory, plies, this);
	}

	public IForkable<M, P> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy);

	public double evaluate(P position, int plies);

	public void stopSearch();

	public void join(P parentPosition, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults);

	public IDepthBasedStrategy<M, P> createCopy();
}
