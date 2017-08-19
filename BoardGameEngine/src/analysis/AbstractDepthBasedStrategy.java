package analysis;

import game.IPosition;

import java.util.List;

public abstract class AbstractDepthBasedStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	protected volatile boolean searchCancelled = false;
	private volatile boolean isSearching = false;
	private volatile int remainingBranches = 0;

	@Override
	public AnalysisResult<M> search(P position, int player, int plies) {
		AnalysisResult<M> analysisResult;
		List<M> possibleMoves;
		synchronized (this) { // so we can't getRemainingBranches() after isSearching until we have counted how many
			isSearching = true;
			searchCancelled = false;
			analysisResult = new AnalysisResult<>();
			if (plies == 0) { // it doesn't really make sense to search 0 deep because this method expects to be able to return scores associated with moves
				isSearching = false;
				return analysisResult;
			}
			possibleMoves = position.getPossibleMoves();
			remainingBranches = possibleMoves.size();
		}
		for (M move : possibleMoves) {
			position.makeMove(move);
			double score = evaluate(position, player, plies - 1);
			position.unmakeMove(move);
			if (searchCancelled) { // we need to check search cancelled after making the call to evaluate
				analysisResult.addUnanalyzedMove(move);
			} else {
				analysisResult.addMoveWithScore(move, score);
			}
			--remainingBranches;
		}
		isSearching = false;
		return analysisResult;
	}

	@Override
	public boolean isSearching() {
		return isSearching;
	}

	@Override
	public void stopSearch() {
		searchCancelled = true;
	}

	@Override
	public synchronized int getRemainingBranches() {
		return remainingBranches;
	}

	@Override
	public void notifySearchStarted() {
		// do nothing by default
	}

	@Override
	public void notifyForked(M parentMove, List<M> unanalyzedMoves) {
		// do nothing by default
	}

	@Override
	public void notifySearchComplete() {
		// do nothing by default
	}
}
