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

	@SuppressWarnings("unchecked")
	public GameRunningState(GameRunner<M, P> gameRunner, IGameRenderer<M, P> gameRenderer) {
		this.gameRunner = gameRunner;
		this.gameRenderer = gameRenderer;
		if (gameRenderer instanceof IPositionObserver<?, ?>) {
			gameRunner.setPositionObserver((IPositionObserver<M, P>) gameRenderer);
		}
		componentResized(GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		graphics.drawImage(boardImage.getImage(), 0, 0, null);
		gameRenderer.drawPosition(graphics, gameRunner.getCurrentPositionCopy(), gameRunner.getPossibleMovesCopy(), gameRunner.getLastMove());
	}

	@Override
	public void componentResized(int width, int height) {
		boardImage.checkResized(width, height);
		gameRenderer.initializeAndDrawBoard(boardImage.getGraphics());
	}

	@Override
	public void handleUserInput(UserInput input) {
		M move = gameRenderer.maybeGetUserMove(input, gameRunner.getCurrentPositionCopy(), gameRunner.getPossibleMovesCopy());
		if (move != null) {
			if (gameRunner.getPossibleMovesCopy().contains(move)) {
				GuiPlayer.HUMAN.setMove(move);
			}
		}
	}
}
