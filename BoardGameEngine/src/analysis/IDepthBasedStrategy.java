package analysis;

import java.util.List;

import game.IPosition;
import util.Pair;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public AnalysisResult<M> search(P position, int player, int plies);

	public boolean isSearching();

	public void stopSearch();

	public AnalysisResult<M> join(P position, int player, List<Pair<M, Double>> movesWithScore, List<Pair<M, AnalysisResult<M>>> results);

	public IDepthBasedStrategy<M, P> createCopy();

	public int getRemainingBranches();
}
