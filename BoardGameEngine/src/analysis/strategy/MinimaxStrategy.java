package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.ArrayMoveList;
import game.IPosition;
import game.MoveList;

public class MinimaxStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public MinimaxStrategy(IPositionEvaluator<M, P> positionEvaluator) {
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		return minimax(position, player, plies);
	}

	private double minimax(P position, int player, int plies) {
		if (searchCanceled) {
			return 0;
		}

		if (plies == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		MoveList<M> possibleMoves = new ArrayMoveList<>(MoveList.MAX_SIZE);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		M move;
		double bestScore;
		int i = 0;
		if (player == position.getCurrentPlayer()) { // Max
			bestScore = Double.NEGATIVE_INFINITY;
			do {
				move = possibleMoves.get(i);
				position.makeMove(move);
				double score = minimax(position, player, plies - 1);
				position.unmakeMove(move);

				if (score > bestScore || (AnalysisResult.isDraw(score) && bestScore < 0) || (AnalysisResult.isDraw(bestScore) && score >= 0)) {
					bestScore = score;
				}

				++i;
			} while (i < numMoves);
		} else { // Min
			bestScore = Double.POSITIVE_INFINITY;
			do {
				move = possibleMoves.get(i);
				position.makeMove(move);
				double score = minimax(position, player, plies - 1);
				position.unmakeMove(move);

				if (score < bestScore || (AnalysisResult.isDraw(score) && bestScore > 0) || (AnalysisResult.isDraw(bestScore) && score <= 0)) {
					bestScore = score;
				}

				++i;
			} while (i < numMoves);
		}

		return bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<>(positionEvaluator);
	}
}
