package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public AlphaBetaStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		return player == position.getCurrentPlayer() ? alphaBeta(position, player, 0, plies, alpha, beta, true) : alphaBeta(position, player, 0, plies, -beta, -alpha, false);
	}

	private double alphaBeta(P position, int player, int ply, int maxPly, double alpha, double beta, boolean max) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0 || ply == maxPly) {
			return positionEvaluator.evaluate(position, possibleMoves, player);
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);
			double ab;
			if (max == (player == position.getCurrentPlayer())) {
				ab = alphaBeta(position, player, ply + 1, maxPly, alpha, beta, player == position.getCurrentPlayer());
			} else {
				ab = alphaBeta(position, player, ply + 1, maxPly, -beta, -alpha, player == position.getCurrentPlayer());
			}
			double score = max ? ab : -ab;

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

		return max ? bestScore : -bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaStrategy<>(moveListFactory, positionEvaluator);
	}
}
