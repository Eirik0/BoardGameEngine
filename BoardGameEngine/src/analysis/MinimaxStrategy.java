package analysis;

import java.util.List;

import analysis.search.MoveWithResult;
import game.IPosition;

public class MinimaxStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private boolean searchedAllPositions = true;

	private final IPositionEvaluator<M, P> positionEvaluator;

	public MinimaxStrategy(IPositionEvaluator<M, P> positionEvaluator) {
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		searchedAllPositions = true;
		return minimax(position, player, plies);
	}

	private double minimax(P position, int player, int plies) {
		if (searchCanceled) {
			return 0;
		}

		if (plies == 0) {
			searchedAllPositions = false;
			return positionEvaluator.evaluate(position, player);
		}

		List<M> possibleMoves = position.getPossibleMoves();

		if (possibleMoves.size() == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		boolean max = player == position.getCurrentPlayer();

		double bestScore = max ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

		for (M move : possibleMoves) {
			if (searchCanceled) {
				return 0;
			}
			position.makeMove(move);
			double score = minimax(position, player, plies - 1);
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
	public double evaluateJoin(P position, int player, MoveWithResult<M> moveWithResult) {
		return player == position.getCurrentPlayer() ? moveWithResult.result.getMin() : moveWithResult.result.getMax();
	}

	@Override
	public boolean searchedAllPositions() {
		return searchedAllPositions;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<>(positionEvaluator);
	}
}
