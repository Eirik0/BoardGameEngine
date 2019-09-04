package bge.strategy.ts.forkjoin.minmax;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.IDepthBasedPositionEvaluator;
import bge.strategy.ts.forkjoin.IForkable;

public class ForkableMinimaxFactory<M, P extends IPosition<M>> implements ForkableTreeSearchFactory<M, P> {
    private final IDepthBasedPositionEvaluator<M, P> strategy;

    public ForkableMinimaxFactory(IDepthBasedPositionEvaluator<M, P> strategy) {
        this.strategy = strategy;
    }

    @Override
    public IForkable<M> createNew(P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies) {
        return new ForkableMinimax<>(position, movesToSearch, moveListFactory, plies, strategy, this);
    }
}