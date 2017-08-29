package game.tictactoe;

import java.awt.Graphics2D;

import game.Coordinate;
import gui.GameGuiManager;
import gui.GuiPlayer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class TicTacToeGameRenderer implements IGameRenderer<Coordinate, TicTacToePosition> {
	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		g.setColor(BoardGameEngineMain.BACKGROUND_COLOR);
		g.fillRect(0, 0, GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
	}

	@Override
	public void drawPosition(Graphics2D g, TicTacToePosition position) {
		int width = GameGuiManager.getComponentWidth();
		int height = GameGuiManager.getComponentHeight();

		g.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		for (int i = 1; i < 3; ++i) {
			int thirdOfWidth = round(i * width / 3.0);
			int thirdOfHeight = round(i * height / 3.0);
			g.drawLine(thirdOfWidth, 0, thirdOfWidth, height);
			g.drawLine(0, thirdOfHeight, width, thirdOfHeight);
		}
		for (int y = 0; y < position.board.length; y++) {
			int[] row = position.board[y];
			for (int x = 0; x < row.length; x++) {
				if (row[x] != 0) {
					String player = row[x] == 1 ? "X" : "O";
					int xCoord = round(width * (2 * x + 1) / 6.0);
					int yCoord = round(height * (2 * y + 1) / 6.0);
					drawCenteredString(g, player, xCoord, yCoord);
				}
			}
		}

	}

	@Override
	public Coordinate maybeGetUserMove(UserInput input, TicTacToePosition position) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			if (GuiPlayer.HUMAN.isRequestingMove()) {
				return getCoordinate(3);
			}
		}
		return null;
	}

	public static Coordinate getCoordinate(double boardSize) {
		int x = (int) (boardSize * GameGuiManager.getMouseX() / GameGuiManager.getComponentWidth());
		int y = (int) (boardSize * GameGuiManager.getMouseY() / GameGuiManager.getComponentHeight());
		return new Coordinate(x, y);
	}
}
