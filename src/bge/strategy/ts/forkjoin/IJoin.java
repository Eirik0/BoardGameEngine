package bge.strategy.ts.forkjoin;

import bge.analysis.AnalysisResult;
import gt.util.Pair;

@FunctionalInterface
public interface IJoin<M> {
    void join(boolean searchCanceled, Pair<M, AnalysisResult<M>> moveWithResult);
}
