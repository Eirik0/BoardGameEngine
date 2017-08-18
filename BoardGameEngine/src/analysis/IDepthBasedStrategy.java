package analysis;

import game.IPosition;

import java.util.List;

import util.Pair;

public interface IDepthBasedStrategy<M, P extends IPosition<M, P>> {
	public AnalysisResult<M> search(P position, int player, int plies);

	public double evaluate(P position, int player, int plies);

	public boolean isSearching();

	public void stopSearch();

	public int getRemainingBranches();

	public AnalysisResult<M> join(P position, int player, List<Pair<M, Double>> movesWithScore, List<Pair<M, AnalysisResult<M>>> results);

	public IDepthBasedStrategy<M, P> createCopy();

	public void notifySearchStarted();

	public void notifyForked(M parentMove, List<M> unanalyzedMoves);
}
