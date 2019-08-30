package bge.gui.gamestate;

import bge.igame.IPosition;
import bge.igame.MoveList;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;

public interface IGameRenderer<M, P extends IPosition<M>> {
    void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight);

    void drawPosition(IGraphics g, P position, MoveList<M> possibleMoves, M lastMove);

    M maybeGetUserMove(UserInput input, P position, MoveList<M> possibleMoves);
}
