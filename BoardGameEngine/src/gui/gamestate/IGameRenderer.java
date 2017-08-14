package gui.gamestate;

import java.awt.Graphics2D;

import game.IPosition;
import gui.DrawingMethods;
import gui.gamestate.GameState.UserInput;

public interface IGameRenderer<M, P extends IPosition<M, P>> extends DrawingMethods {
	public void initializeAndDrawBoard(Graphics2D g);

	public void drawPosition(Graphics2D g, P position);

	public M maybeGetMove(UserInput input, P position);
}
