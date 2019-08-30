package bge.gui.gamestate;

import bge.igame.IPosition;
import bge.igame.MoveList;

public interface IPositionObserver<M, P extends IPosition<M>> {
    public void notifyPositionChanged(P position, MoveList<M> possibleMoves);
}
