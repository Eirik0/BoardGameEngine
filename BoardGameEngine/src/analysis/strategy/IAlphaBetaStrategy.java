package analysis.strategy;

import game.IPosition;

public interface IAlphaBetaStrategy<M, P extends IPosition<M>> extends IDepthBasedStrategy<M, P> {
	@Override
	default double evaluate(P position, int plies) {
		return evaluate(position, plies, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public double evaluate(P position, int plies, double alpha, double beta);
}
