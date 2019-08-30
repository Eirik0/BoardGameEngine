package bge.gui.gamestate;

import java.awt.Graphics2D;

import bge.game.IPosition;
import bge.game.MoveList;
import bge.gui.DrawingMethods;
import gt.gamestate.UserInput;

public interface IGameRenderer<M, P extends IPosition<M>> extends DrawingMethods {
    void initializeAndDrawBoard(Graphics2D g);

    void drawPosition(Graphics2D g, P position, MoveList<M> possibleMoves, M lastMove);

    M maybeGetUserMove(UserInput input, P position, MoveList<M> possibleMoves);
}
