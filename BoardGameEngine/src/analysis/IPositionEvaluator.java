package analysis;

import game.IPosition;
import game.MoveList;

public interface IPositionEvaluator<M, P extends IPosition<M, P>> {
	public double evaluate(P position, MoveList<M> possibleMoves);
}
