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
	public double evaluate(P position, int plies) {
		return alphaBeta(position, 0, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	private double alphaBeta(P position, int ply, int maxPly, double alpha, double beta) {
		if (searchCanceled) {
			return 0;
		}

		MoveList<M> possibleMoves = getMoveList(ply);
		position.getPossibleMoves(possibleMoves);
		int numMoves = possibleMoves.size();

		if (numMoves == 0 || ply == maxPly) {
			return positionEvaluator.evaluate(position, possibleMoves);
		}

		int parentPlayer = position.getCurrentPlayer();

		double bestScore = Double.NEGATIVE_INFINITY;
		int i = 0;
		do {
			M move = possibleMoves.get(i);
			position.makeMove(move);
			double score = parentPlayer == position.getCurrentPlayer() ? alphaBeta(position, ply + 1, maxPly, alpha, beta) : -alphaBeta(position, ply + 1, maxPly, -beta, -alpha);
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

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new AlphaBetaStrategy<>(moveListFactory, positionEvaluator);
	}
}
