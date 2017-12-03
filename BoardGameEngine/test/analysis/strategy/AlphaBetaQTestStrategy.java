package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaQTestStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public AlphaBetaQTestStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		return player == position.getCurrentPlayer() ? max(position, player, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)
				: min(position, player, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	private double max(P position, int player, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (ply == maxPly) {
			return maxQ(position, player, maxPly, alpha, beta);
		} else if (numMoves == 0) {
			return positionEvaluator.evaluate(position, possibleMoves, player);
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);

			double score;
			if (player == position.getCurrentPlayer()) {
				score = max(position, player, ply + 1, maxPly, alpha, beta);
			} else {
				score = min(position, player, ply + 1, maxPly, alpha, beta);
			}

			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
					break;
				}
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double min(P position, int player, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (ply == maxPly) {
			return minQ(position, player, maxPly, alpha, beta);
		} else if (numMoves == 0) {
			return positionEvaluator.evaluate(position, possibleMoves, player);
		}

		double bestScore = Double.POSITIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);

			double score;
			if (player == position.getCurrentPlayer()) {
				score = max(position, player, ply + 1, maxPly, alpha, beta);
			} else {
				score = min(position, player, ply + 1, maxPly, alpha, beta);
			}

			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
					break;
				}
				if (AnalysisResult.isGreater(beta, score)) { // score < beta
					beta = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double maxQ(P position, int player, int ply, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.numDynamicMoves();

		double currentScore = positionEvaluator.evaluate(position, possibleMoves, player);
		if (numMoves == 0) {
			return currentScore;
		}
		if (!AnalysisResult.isGreater(beta, currentScore)) {
			return beta;
		}
		if (AnalysisResult.isGreater(currentScore, alpha)) {
			alpha = currentScore;
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);

			double score;
			if (player == position.getCurrentPlayer()) {
				score = maxQ(position, player, ply + 1, alpha, beta);
			} else {
				score = minQ(position, player, ply + 1, alpha, beta);
			}

			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
					break;
				}
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double minQ(P position, int player, int ply, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.numDynamicMoves();

		double currentScore = positionEvaluator.evaluate(position, possibleMoves, player);
		if (numMoves == 0) {
			return currentScore;
		}
		if (!AnalysisResult.isGreater(currentScore, alpha)) { // alpha >= beta
			return alpha;
		}
		if (AnalysisResult.isGreater(beta, currentScore)) { // score < beta
			beta = currentScore;
		}

		double bestScore = Double.POSITIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);

			double score;
			if (player == position.getCurrentPlayer()) {
				score = maxQ(position, player, ply + 1, alpha, beta);
			} else {
				score = minQ(position, player, ply + 1, alpha, beta);
			}

			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
					break;
				}
				if (AnalysisResult.isGreater(beta, score)) { // score < beta
					beta = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaQTestStrategy<>(moveListFactory, positionEvaluator);
	}
}
