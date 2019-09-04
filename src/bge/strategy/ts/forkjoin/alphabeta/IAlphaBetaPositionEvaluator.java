package bge.strategy.ts.forkjoin.alphabeta;

import bge.analysis.AnalysisResult;
import bge.igame.IPosition;
import bge.strategy.ts.forkjoin.IDepthBasedPositionEvaluator;

public interface IAlphaBetaPositionEvaluator<M, P extends IPosition<M>> extends IDepthBasedPositionEvaluator<M, P> {
    @Override
    default double evaluate(P position, int plies) {
        return evaluate(position, plies, AnalysisResult.LOSS, AnalysisResult.WIN);
    }

    double evaluate(P position, int plies, double alpha, double beta);

    @Override
    IAlphaBetaPositionEvaluator<M, P> createCopy();
}