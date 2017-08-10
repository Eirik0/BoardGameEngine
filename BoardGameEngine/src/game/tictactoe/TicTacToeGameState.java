package game.tictactoe;

import java.awt.Color;
import java.awt.Graphics2D;

import game.Coordinate;
import game.GameRunner;
import gui.GameGuiManager;
import gui.GameState;
import gui.GuiPlayer;

public class TicTacToeGameState implements GameState {
	private final GameRunner<Coordinate, TicTacToePosition> gameRunner;

	public TicTacToeGameState(GameRunner<Coordinate, TicTacToePosition> gameRunner) {
		this.gameRunner = gameRunner;
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		int width = GameGuiManager.getComponentWidth();
		int height = GameGuiManager.getComponentHeight();

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);

		graphics.setColor(Color.BLACK);
		for (int i = 1; i < 3; ++i) {
			int thirdOfWidth = round(i * width / 3.0);
			int thirdOfHeight = round(i * height / 3.0);
			graphics.drawLine(thirdOfWidth, 0, thirdOfWidth, height);
			graphics.drawLine(0, thirdOfHeight, width, thirdOfHeight);
		}
		TicTacToePosition currentPosition = gameRunner.getCurrentPosition();
		for (int y = 0; y < currentPosition.board.length; y++) {
			int[] row = currentPosition.board[y];
			for (int x = 0; x < row.length; x++) {
				if (row[x] != 0) {
					String player = row[x] == 1 ? "X" : "O";
					int xCoord = round(width * (2 * x + 1) / 6.0);
					int yCoord = round(height * (2 * y + 1) / 6.0);
					drawCenteredString(graphics, player, xCoord, yCoord);
				}
			}
		}
	}

	@Override
	public void handleUserInput(UserInput input) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			if (GuiPlayer.HUMAN.isRequestingMove()) {
				GuiPlayer.HUMAN.setMove(getCoordinate());
			}
		}
	}

	private Coordinate getCoordinate() {
		int x = (int) (3.0 * GameGuiManager.getMouseX() / GameGuiManager.getComponentWidth());
		int y = (int) (3.0 * GameGuiManager.getMouseY() / GameGuiManager.getComponentHeight());
		return new Coordinate(x, y);
	}
}
