package analysis;

import java.util.List;

import game.IPosition;
import util.Pair;

public class MinimaxStrategy<M, P extends IPosition<M, P>> implements IDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	private volatile boolean searchCanceled = false;
	private volatile boolean isSearching = false;
	private volatile int remainingBranches = 0;

	public MinimaxStrategy(IPositionEvaluator<M, P> positionEvaluator) {
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public AnalysisResult<M> search(P position, int player, int plies) {
		AnalysisResult<M> analysisResult;
		List<M> possibleMoves;
		synchronized (this) { // so we can't getRemainingBranches() after isSearching until we have counted how many
			isSearching = true;
			searchCanceled = false;
			analysisResult = new AnalysisResult<M>();
			if (plies == 0) {
				isSearching = false;
				return analysisResult;
			}

			possibleMoves = position.getPossibleMoves();
			remainingBranches = possibleMoves.size();
		}

		for (M move : possibleMoves) {
			position.makeMove(move);
			double score = minimax(position, positionEvaluator, player, plies - 1);
			position.unmakeMove(move);
			if (searchCanceled) {
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
	public synchronized int getRemainingBranches() {
		return remainingBranches;
	}

	private double minimax(P position, IPositionEvaluator<M, P> positionEvaluator, int player, int plies) {
		if (searchCanceled) {
			return 0;
		}

		if (plies == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		List<M> possibleMoves = position.getPossibleMoves();

		if (possibleMoves.size() == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		boolean max = player == position.getCurrentPlayer();

		double bestScore = max ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

		for (M move : possibleMoves) {
			position.makeMove(move);
			double score = minimax(position, positionEvaluator, player, plies - 1);
			position.unmakeMove(move);

			if (max) {
				if (score > bestScore) {
					bestScore = score;
				}
			} else {
				if (score < bestScore) {
					bestScore = score;
				}
			}
		}

		return bestScore;
	}

	@Override
	public void stopSearch() {
		searchCanceled = true;
	}

	@Override
	public boolean isSearching() {
		return isSearching;
	}

	@Override
	public AnalysisResult<M> join(P position, int player, List<Pair<M, Double>> movesWithScore, List<Pair<M, AnalysisResult<M>>> results) {
		AnalysisResult<M> joinedResult = new AnalysisResult<>();
		for (Pair<M, Double> moveWithScore : movesWithScore) {
			joinedResult.addMoveWithScore(moveWithScore.getFirst(), moveWithScore.getSecond());
		}
		boolean min = player == position.getCurrentPlayer();
		for (Pair<M, AnalysisResult<M>> analysisResult : results) {
			if (min) {
				joinedResult.addMoveWithScore(analysisResult.getFirst(), analysisResult.getSecond().getMin());
			} else {
				joinedResult.addMoveWithScore(analysisResult.getFirst(), analysisResult.getSecond().getMax());
			}
		}
		return joinedResult;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<M, P>(positionEvaluator);
	}
}
