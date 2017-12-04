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
	public double evaluate(P position, int plies) {
		return max(position, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	private double max(P position, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (ply == maxPly) {
			return maxQ(position, maxPly, alpha, beta);
		} else if (numMoves == 0) {
			return positionEvaluator.evaluate(position, possibleMoves);
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.NEGATIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? max(position, ply + 1, maxPly, alpha, beta) : min(position, ply + 1, maxPly, alpha, beta);
			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
					return beta;
				}
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double min(P position, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (ply == maxPly) {
			return minQ(position, maxPly, alpha, beta);
		} else if (numMoves == 0) {
			return -positionEvaluator.evaluate(position, possibleMoves);
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.POSITIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? min(position, ply + 1, maxPly, alpha, beta) : max(position, ply + 1, maxPly, alpha, beta);
			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
					return alpha;
				}
				if (AnalysisResult.isGreater(beta, score)) { // score < beta
					beta = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double maxQ(P position, int ply, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.numDynamicMoves();

		double currentScore = positionEvaluator.evaluate(position, possibleMoves);
		if (numMoves == 0) {
			return currentScore;
		}
		if (!AnalysisResult.isGreater(beta, currentScore)) {
			return currentScore;
		}
		if (AnalysisResult.isGreater(currentScore, alpha)) {
			alpha = currentScore;
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.NEGATIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? maxQ(position, ply + 1, alpha, beta) : minQ(position, ply + 1, alpha, beta);
			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta (fail-soft)
					return beta;
				}
				if (AnalysisResult.isGreater(score, alpha)) {
					alpha = score;
				}
			}
			++i;
		} while (i < numMoves);

		return bestScore;
	}

	private double minQ(P position, int ply, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.numDynamicMoves();

		double currentScore = -positionEvaluator.evaluate(position, possibleMoves);
		if (numMoves == 0) {
			return currentScore;
		}
		if (!AnalysisResult.isGreater(currentScore, alpha)) { // alpha >= beta
			return currentScore;
		}
		if (AnalysisResult.isGreater(beta, currentScore)) { // score < beta
			beta = currentScore;
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.POSITIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? minQ(position, ply + 1, alpha, beta) : maxQ(position, ply + 1, alpha, beta);
			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(bestScore, alpha)) { // alpha >= beta
					return alpha;
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
