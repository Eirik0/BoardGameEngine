package bge.strategy.ts.forkjoin.alphabeta;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.IForkable;

public class ForkableAlphaBetaFactory<M, P extends IPosition<M>> implements ForkableTreeSearchFactory<M, P> {
    private final IAlphaBetaPositionEvaluator<M, P> strategy;

    public ForkableAlphaBetaFactory(IAlphaBetaPositionEvaluator<M, P> strategy) {
        this.strategy = strategy;
    }

    @Override
    public IForkable<M> createNew(P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies) {
        return new ForkableAlphaBeta<>(position, movesToSearch, moveListFactory, plies, strategy, this);
    }
}
