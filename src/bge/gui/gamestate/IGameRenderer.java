package bge.gui.gamestate;

import java.awt.Graphics2D;

import bge.game.IPosition;
import bge.game.MoveList;
import bge.gui.DrawingMethods;
import bge.gui.gamestate.GameState.UserInput;

public interface IGameRenderer<M, P extends IPosition<M>> extends DrawingMethods {
    public void initializeAndDrawBoard(Graphics2D g);

    public void drawPosition(Graphics2D g, P position, MoveList<M> possibleMoves, M lastMove);

    public M maybeGetUserMove(UserInput input, P position, MoveList<M> possibleMoves);
}
