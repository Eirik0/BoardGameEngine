package game.tictactoe;

import java.awt.Color;
import java.awt.Graphics2D;

import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;
import gui.GameGuiManager;
import gui.GuiPlayer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class TicTacToeGameRenderer implements IGameRenderer<Coordinate, TicTacToePosition> {
	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int width = GameGuiManager.getComponentWidth();
		int height = GameGuiManager.getComponentHeight();

		fillRect(g, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);

		g.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
		for (int i = 1; i < 3; ++i) {
			int thirdOfWidth = round(i * width / 3.0);
			int thirdOfHeight = round(i * height / 3.0);
			g.drawLine(thirdOfWidth, 0, thirdOfWidth, height);
			g.drawLine(0, thirdOfHeight, width, thirdOfHeight);
		}
	}

	@Override
	public void drawPosition(Graphics2D g, TicTacToePosition position, MoveList<Coordinate> possibleMoves, Coordinate lastMove) {
		int width = GameGuiManager.getComponentWidth();
		int height = GameGuiManager.getComponentHeight();
		for (int y = 0; y < TicTacToePosition.BOARD_WIDTH; ++y) {
			for (int x = 0; x < TicTacToePosition.BOARD_WIDTH; ++x) {
				int playerInt = getPlayer(position.board, x, y);
				if (playerInt != TwoPlayers.UNPLAYED) {
					g.setColor(lastMove != null && Coordinate.valueOf(x, y).equals(lastMove) ? Color.RED : BoardGameEngineMain.FOREGROUND_COLOR);
					String player = playerInt == TwoPlayers.PLAYER_1 ? "X" : "O";
					int xCoord = round(width * (2 * x + 1) / 6.0);
					int yCoord = round(height * (2 * y + 1) / 6.0);
					drawCenteredString(g, player, xCoord, yCoord);
				}
			}
		}
	}

	private int getPlayer(int board, int x, int y) {
		int shift = (y * TicTacToePosition.BOARD_WIDTH + x) * 2;
		return (board >> shift) & TwoPlayers.BOTH_PLAYERS;
	}

	@Override
	public Coordinate maybeGetUserMove(UserInput input, TicTacToePosition position, MoveList<Coordinate> possibleMoves) {
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
		return Coordinate.valueOf(x, y);
	}
}
