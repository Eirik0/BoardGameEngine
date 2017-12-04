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
	public double evaluate(P position, int plies) {
		return negamax(position, 0, plies);
	}

	private double negamax(P position, int ply, int maxPly) {
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
			double score = parentPlayer == position.getCurrentPlayer() ? negamax(position, ply + 1, maxPly) : -negamax(position, ply + 1, maxPly);
			position.unmakeMove(move);

			if (AnalysisResult.isGreater(score, bestScore)) {
				bestScore = score;
			}

			++i;
		} while (i < numMoves);

		return bestScore;
	}

	@Override
	public IDepthBasedStrategy<M, P> createCopy() {
		return new MinimaxStrategy<>(moveListFactory, positionEvaluator);
	}
}
