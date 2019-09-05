package bge.strategy.ts.forkjoin;

import bge.analysis.IPositionEvaluator;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.alphabeta.AlphaBetaPositionEvaluator;
import bge.strategy.ts.forkjoin.alphabeta.AlphaBetaQPositionEvaluator;
import bge.strategy.ts.forkjoin.alphabeta.ForkableAlphaBeta;
import bge.strategy.ts.forkjoin.alphabeta.IAlphaBetaPositionEvaluator;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimax;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;

public class ForkableTreeSearchFactory<M, P extends IPosition<M>> {
    public static enum ForkableType {
        MINIMAX, ALPHA_BETA, ALPHA_BETA_Q
    }

    private final ForkableType forkableType;
    protected final IPositionEvaluator<M, P> positionEvaluator;
    protected final MoveListFactory<M> moveListFactory;

    public ForkableTreeSearchFactory(ForkableType forkableType, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory) {
        this.forkableType = forkableType;
        this.positionEvaluator = positionEvaluator;
        this.moveListFactory = moveListFactory;
    }

    public IDepthBasedPositionEvaluator<M, P> newStrategy() {
        switch (forkableType) {
        case MINIMAX:
            return new MinimaxPositionEvaluator<>(positionEvaluator, moveListFactory);
        case ALPHA_BETA:
            return new AlphaBetaPositionEvaluator<>(positionEvaluator, moveListFactory);
        case ALPHA_BETA_Q: // TODO
            return new AlphaBetaQPositionEvaluator<>(positionEvaluator, moveListFactory);
        default:
            throw new IllegalStateException("Unknown ForkableType: " + forkableType);
        }
    }

    public IForkable<M> createNew(P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies) {
        switch (forkableType) {
        case MINIMAX:
            return new ForkableMinimax<>(position, movesToSearch, moveListFactory, plies, newStrategy(), this);
        case ALPHA_BETA:
        case ALPHA_BETA_Q:
            return new ForkableAlphaBeta<>(position, movesToSearch, moveListFactory, plies, (IAlphaBetaPositionEvaluator<M, P>) newStrategy(), this);
        default:
            throw new IllegalStateException("Unknown ForkableType: " + forkableType);
        }
    }
}
