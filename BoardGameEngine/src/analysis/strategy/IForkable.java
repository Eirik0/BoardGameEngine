package analysis.strategy;

import java.util.List;

import analysis.AnalysisResult;
import analysis.search.GameTreeSearch;
import analysis.search.IGameTreeSearchJoin;
import game.IPosition;

public interface IForkable<M, P extends IPosition<M>> {
	public AnalysisResult<M> search();

	public void stopSearch();

	public int getPlayer();

	public M getParentMove();

	public int getPlies();

	public int getRemainingBranches();

	public default boolean isForkable() {
		return getPlies() > 0 && getRemainingBranches() > 0;
	}

	public List<GameTreeSearch<M, P>> fork(IGameTreeSearchJoin<M> parentJoin, AnalysisResult<M> partialResult);
}
