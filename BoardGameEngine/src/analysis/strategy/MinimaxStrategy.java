package analysis.strategy;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class MinimaxStrategy<M, P extends IPosition<M, P>> extends AbstractDepthBasedStrategy<M, P> {
	private final IPositionEvaluator<M, P> positionEvaluator;

	public MinimaxStrategy(MoveListFactory<M> moveListFactory, IPositionEvaluator<M, P> positionEvaluator) {
		super(moveListFactory);
		this.positionEvaluator = positionEvaluator;
	}

	@Override
	public double evaluate(P position, int player, int plies) {
		return negamax(position, player, 0, plies, player == position.getCurrentPlayer());
	}

	private double negamax(P position, int player, int ply, int maxPly, boolean max) {
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
			double negamax = negamax(position, player, ply + 1, maxPly, player == position.getCurrentPlayer());
			double score = max ? negamax : -negamax;
			position.unmakeMove(move);

			if (score > bestScore || (AnalysisResult.isDraw(score) && bestScore < 0) || (AnalysisResult.isDraw(bestScore) && score >= 0)) {
				bestScore = score;
			}

			++i;
		} while (i < numMoves);

		return max ? bestScore : -bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<>(moveListFactory, positionEvaluator);
	}
}
