package bge.analysis.strategy;

import java.util.List;

import bge.analysis.AnalysisResult;
import bge.analysis.search.GameTreeSearch;
import bge.analysis.search.IGameTreeSearchJoin;

public interface IForkable<M> {
    AnalysisResult<M> search(IGameTreeSearchJoin<M> join);

    void stopSearch();

    M getParentMove();

    int getPlies();

    int getRemainingBranches();

    default boolean isForkable() {
        return getPlies() > 0 && getRemainingBranches() > 0;
    }

    List<GameTreeSearch<M>> fork(IGameTreeSearchJoin<M> parentJoin, AnalysisResult<M> partialResult);
}
