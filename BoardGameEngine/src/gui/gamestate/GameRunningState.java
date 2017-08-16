package gui.gamestate;

import java.awt.Graphics2D;

import game.GameRunner;
import game.IPosition;
import gui.GameGuiManager;
import gui.GameImage;
import gui.GuiPlayer;

public class GameRunningState<M, P extends IPosition<M, P>> implements GameState {
	private final GameRunner<M, P> gameRunner;
	private final IGameRenderer<M, P> gameRenderer;
	private final GameImage boardImage = new GameImage();

	public GameRunningState(GameRunner<M, P> gameRunner, IGameRenderer<M, P> gameRenderer) {
		this.gameRunner = gameRunner;
		this.gameRenderer = gameRenderer;
		componentResized();
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		graphics.drawImage(boardImage.getImage(), 0, 0, null);
		gameRenderer.drawPosition(graphics, gameRunner.getCurrentPosition());
	}

	@Override
	public void componentResized() {
		boardImage.checkResized(GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
		gameRenderer.initializeAndDrawBoard(boardImage.getGraphics());
	}

	@Override
	public void handleUserInput(UserInput input) {
		M move = gameRenderer.maybeGetUserMove(input, gameRunner.getCurrentPosition());
		if (move != null) {
			if (gameRunner.getCurrentPosition().getPossibleMoves().contains(move)) {
				GuiPlayer.HUMAN.setMove(move);
			}
		}
	}
}
