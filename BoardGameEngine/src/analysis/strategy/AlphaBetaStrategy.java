package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final MoveListFactory<M> moveListFactory;
	private final IPositionEvaluator<M, P> positionEvaluator;

	public AlphaBetaStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		this.moveListFactory = moveListFactory;
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		initMoveLists(moveListFactory, plies);
		return alphaBeta(position, player, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	private double alphaBeta(P position, int player, int plies, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		if (plies == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		MoveList<M> possibleMoves = getMoveList(plies);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0) {
			return positionEvaluator.evaluate(position, player);
		}

		boolean max = player == position.getCurrentPlayer();

		M move;
		double bestScore;
		int i = 0;
		if (max) { // Max
			bestScore = Double.NEGATIVE_INFINITY;
			do {
				move = possibleMoves.get(i);
				position.makeMove(move);
				double score = alphaBeta(position, player, plies - 1, alpha, beta);
				position.unmakeMove(move);

				if (score > bestScore || (AnalysisResult.isDraw(score) && bestScore < 0) || (AnalysisResult.isDraw(bestScore) && score >= 0)) {
					bestScore = score;
					if (bestScore > alpha) {
						alpha = bestScore;
						if (beta <= alpha) {
							break;
						}
					}
				}

				++i;
			} while (i < numMoves);
		} else { // Min
			bestScore = Double.POSITIVE_INFINITY;
			do {
				move = possibleMoves.get(i);
				position.makeMove(move);
				double score = alphaBeta(position, player, plies - 1, alpha, beta);
				position.unmakeMove(move);

				if (score < bestScore || (AnalysisResult.isDraw(score) && bestScore > 0) || (AnalysisResult.isDraw(bestScore) && score <= 0)) {
					bestScore = score;
					if (bestScore < beta) {
						beta = bestScore;
						if (beta <= alpha) {
							break;
						}
					}
				}

				++i;
			} while (i < numMoves);
		}

		return bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaStrategy<>(moveListFactory, positionEvaluator.createCopy());
	}
}
