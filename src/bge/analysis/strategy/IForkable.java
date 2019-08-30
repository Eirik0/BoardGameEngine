package bge.analysis.strategy;

import java.util.List;

import bge.analysis.AnalysisResult;
import bge.analysis.search.GameTreeSearch;
import bge.analysis.search.IGameTreeSearchJoin;
import bge.igame.IPosition;

public interface IForkable<M, P extends IPosition<M>> {
    public AnalysisResult<M> search(IGameTreeSearchJoin<M> join);

    public void stopSearch();

    public M getParentMove();

    public int getPlies();

    public int getRemainingBranches();

    public default boolean isForkable() {
        return getPlies() > 0 && getRemainingBranches() > 0;
    }

    public List<GameTreeSearch<M, P>> fork(IGameTreeSearchJoin<M> parentJoin, AnalysisResult<M> partialResult);
}
