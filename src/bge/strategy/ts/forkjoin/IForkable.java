package bge.strategy.ts.forkjoin;

import java.util.List;

import bge.analysis.AnalysisResult;

public interface IForkable<M> {
    AnalysisResult<M> search();

    void stopSearch();

    int getPlies();

    int getRemainingBranches();

    default boolean isForkable() {
        return getPlies() > 0 && getRemainingBranches() > 0;
    }

    List<ForkJoinNode<M>> fork(IJoin<M> parentJoin, M parentMove, AnalysisResult<M> currentPartial);
}
