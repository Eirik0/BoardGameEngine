package analysis;

import game.IPosition;

public interface IPositionEvaluator<M, P extends IPosition<M, P>> {
	public double evaluate(P position, int player);

	public IPositionEvaluator<M, P> createCopy();
}
