package bge.analysis.strategy;

import java.util.Map;

import bge.analysis.AnalysisResult;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;

public interface IDepthBasedStrategy<M, P extends IPosition<M>> {
    default IForkable<M> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies) {
        return newForkableSearch(parentMove, position, movesToSearch, moveListFactory, plies, this);
    }

    IForkable<M> newForkableSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies,
            IDepthBasedStrategy<M, P> strategy);

    double evaluate(P position, int plies);

    void stopSearch();

    void join(P parentPosition, AnalysisResult<M> partialResult, Map<M, AnalysisResult<M>> movesWithResults);

    IDepthBasedStrategy<M, P> createCopy();
}
