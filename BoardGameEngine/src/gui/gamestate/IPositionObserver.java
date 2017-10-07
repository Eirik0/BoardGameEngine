package gui.gamestate;

import java.util.List;

import game.IPosition;

public interface IPositionObserver<M, P extends IPosition<M, P>> {
	public void notifyPositionChanged(P position, List<M> possibleMoves);
}
