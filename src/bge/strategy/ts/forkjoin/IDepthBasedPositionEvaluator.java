package bge.strategy.ts.forkjoin;

import bge.igame.IPosition;

public interface IDepthBasedPositionEvaluator<M, P extends IPosition<M>> {
    double evaluate(P position, int plies);

    void stopSearch();
}
