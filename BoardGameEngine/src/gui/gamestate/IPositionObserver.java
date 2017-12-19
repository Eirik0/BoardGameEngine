package gui.gamestate;

import game.IPosition;
import game.MoveList;

public interface IPositionObserver<M, P extends IPosition<M>> {
	public void notifyPositionChanged(P position, MoveList<M> possibleMoves);
}
