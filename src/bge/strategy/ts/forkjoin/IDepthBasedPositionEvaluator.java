package bge.strategy.ts.forkjoin;

import bge.igame.IDeepCopy;
import bge.igame.IPosition;

public interface IDepthBasedPositionEvaluator<M, P extends IPosition<M>> extends IDeepCopy<IDepthBasedPositionEvaluator<M, P>> {
    double evaluate(P position, int plies);

    void stopSearch();
}
