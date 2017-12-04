package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class AlphaBetaQStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public AlphaBetaQStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int plies) {
		return alphaBeta(position, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
	}

	private double alphaBeta(P position, int ply, int maxPly, double alpha, double beta, boolean quiescent) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numDynamicMoves = possibleMoves.numDynamicMoves();
		int numMoves = quiescent ? numDynamicMoves : possibleMoves.size();

		if (numMoves == 0 || ply == maxPly || quiescent) {
			double score = positionEvaluator.evaluate(position, possibleMoves);
			if (numDynamicMoves == 0) {
				return score;
			}
			if (!AnalysisResult.isGreater(beta, score)) { // score >= beta
				return score;
			}
			if (AnalysisResult.isGreater(score, alpha)) {
				alpha = score;
			}
			numMoves = numDynamicMoves;
			quiescent = true;
			++maxPly;
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.NEGATIVE_INFINITY;
		M move;
		int i = 0;
		do {
			move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, ply + 1, maxPly, alpha, beta, quiescent) : -alphaBeta(position, ply + 1, maxPly, -beta, -alpha, quiescent);
			position.unmakeMove(move);

			if (!AnalysisResult.isGreater(bestScore, score)) {
				bestScore = score;
				if (!AnalysisResult.isGreater(beta, bestScore)) { // alpha >= beta
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

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaQStrategy<>(moveListFactory, positionEvaluator);
	}
}
