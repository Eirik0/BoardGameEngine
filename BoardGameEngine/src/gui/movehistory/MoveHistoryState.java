package gui.movehistory;

import java.awt.Graphics2D;

import game.GameRunner;
import game.IPosition;
import game.MoveHistory;
import gui.MouseTracker;
import gui.gamestate.GameState;
import gui.movehistory.GuiMoveHistory.MoveMenuItem;
import main.BoardGameEngineMain;

public class MoveHistoryState<M, P extends IPosition<M, P>> implements GameState {
	int width;
	int height;

	public final MouseTracker mouseTracker = new MouseTracker(this::handleUserInput);
	private GameRunner<M, P> gameRunner;
	private GuiMoveHistory<M> guiMoveHistory;

	public MoveHistoryState() {
		guiMoveHistory = new GuiMoveHistory<>(new MoveHistory<>(1), mouseTracker);
	}

	public void setGameRunner(GameRunner<M, P> gameRunner) {
		this.gameRunner = gameRunner;
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		guiMoveHistory.drawOn(graphics);
	}

	public void setMoveHistory(MoveHistory<M, P> moveHistory) {
		guiMoveHistory = new GuiMoveHistory<>(moveHistory, mouseTracker);
	}

	@Override
	public void componentResized(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void handleUserInput(UserInput input) {
		if (UserInput.LEFT_BUTTON_RELEASED == input) {
			MoveMenuItem selectedMove = guiMoveHistory.getSelectedMove();
			if (selectedMove != null) {
				gameRunner.setPositionFromHistory(selectedMove.moveNum, selectedMove.playerNum);
			}
		}
	}
}
