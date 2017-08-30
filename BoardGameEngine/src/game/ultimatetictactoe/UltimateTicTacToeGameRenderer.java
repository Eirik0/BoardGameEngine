package game.ultimatetictactoe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import game.Coordinate;
import gui.GameGuiManager;
import gui.GuiPlayer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class UltimateTicTacToeGameRenderer implements IGameRenderer<UTTTCoordinate, UltimateTicTacToePosition> {
	private static final Color WOOD_COLOR = new Color(166, 128, 100);

	private int boardWidth = 0;
	private int smallBoardWidth = 0;
	private double offsetX = 0;
	private double offsetY = 0;
	private double cellWidth = 0;

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int imageWidth = GameGuiManager.getComponentWidth();
		int imageHeight = GameGuiManager.getComponentHeight();

		boardWidth = Math.min(imageWidth, imageHeight);
		smallBoardWidth = boardWidth / 3;

		offsetX = (imageWidth - boardWidth) / 2;
		offsetY = (imageHeight - boardWidth) / 2;
		cellWidth = boardWidth / 9;

		g.setColor(BoardGameEngineMain.BACKGROUND_COLOR);
		g.fillRect(0, 0, imageWidth, imageHeight);

		g.setColor(WOOD_COLOR);
		g.fillRect(round(offsetX), round(offsetY), boardWidth, boardWidth);

		int padding = getSquareWidthFraction(.1);

		g.setColor(Color.BLACK);
		drawBoard(g, offsetX, offsetY, boardWidth, padding, 4);

		for (int x = 0; x < 3; ++x) {
			for (int y = 0; y < 3; ++y) {
				drawBoard(g, offsetX + smallBoardWidth * x, offsetY + smallBoardWidth * y, smallBoardWidth, padding, 3);
			}
		}
	}

	private void drawBoard(Graphics g, double x0, double y0, double width, double padding, int lineThickness) {
		drawThickVLike(g, x0 + width / 3, y0 + padding, y0 + width - padding, lineThickness);
		drawThickVLike(g, x0 + 2 * width / 3, y0 + padding, y0 + width - padding, lineThickness);
		drawThickHLike(g, y0 + width / 3, x0 + padding, x0 + width - padding, lineThickness);
		drawThickHLike(g, y0 + 2 * width / 3, x0 + padding, x0 + width - padding, lineThickness);
	}

	private void drawThickHLike(Graphics g, double y, double x0, double x1, double thickness) {
		g.fillRect(round(x0), round(y - thickness / 2), round(x1 - x0), round(thickness));
	}

	private void drawThickVLike(Graphics g, double x, double y0, double y1, double thickness) {
		g.fillRect(round(x - thickness / 2), round(y0), round(thickness), round(y1 - y0));
	}

	@Override
	public void drawPosition(Graphics2D g, UltimateTicTacToePosition position) {
		drawMoves(g, position);
		highlightBoardInPlay(g, position);
		drawMouseOn(g, position);
	}

	private void drawMoves(Graphics2D g, UltimateTicTacToePosition position) {
		g.setColor(Color.BLACK);
		Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, getSquareWidthFraction(.33));
		for (int n = 0; n < UltimateTicTacToePosition.BOARD_WIDTH; ++n) {
			for (int m = 0; m < UltimateTicTacToePosition.BOARD_WIDTH; ++m) {
				if (position.cells[n][m] != UltimateTicTacToePosition.UNPLAYED) {
					Coordinate intersection = UltimateTicTacToeUtilities.getBoardNM(m, n);
					String player = position.cells[n][m] == UltimateTicTacToePosition.PLAYER_1 ? "X" : "O";
					drawCenteredString(g, smallFont, player, getCenterX(intersection.y), getCenterY(intersection.x));
				}
			}
		}
		Font largeFont = new Font(Font.SANS_SERIF, Font.PLAIN, getSquareWidthFraction(3));
		for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
			if (position.wonBoards[i] != UltimateTicTacToePosition.UNPLAYED) {
				String player = position.wonBoards[i] == UltimateTicTacToePosition.PLAYER_1 ? "X" : "O";
				Coordinate intersection = UltimateTicTacToeUtilities.getBoardXY(i, 4); // 4 = the center square of that board
				drawCenteredString(g, largeFont, player, getCenterX(intersection.x), getCenterY(intersection.y));
			}
		}
	}

	private void highlightBoardInPlay(Graphics2D g, UltimateTicTacToePosition position) {
		// draw the current board in play
		g.setColor(Color.BLUE);
		int padding = getSquareWidthFraction(.05);
		if (position.currentBoard == UltimateTicTacToePosition.ANY_BOARD) {
			int highlightWidth = boardWidth - 2 * padding;
			g.drawRect(round(offsetX) + padding, round(offsetY) + padding, highlightWidth, highlightWidth);
		} else {
			Coordinate boardXY = UltimateTicTacToeUtilities.getBoardXY(position.currentBoard, 0); // 0 = upper left square
			int highlightWidth = smallBoardWidth - 2 * padding;
			g.drawRect(getSquareCornerX(boardXY.x) + padding, getSquareCornerY(boardXY.y) + padding, highlightWidth, highlightWidth);
		}
	}

	private void drawMouseOn(Graphics g, UltimateTicTacToePosition position) {
		if (GuiPlayer.HUMAN.isRequestingMove() && GameGuiManager.isMouseEntered()) {// highlight the cell if the mouse if over a playable move
			Coordinate coordinate = getCoordinate(GameGuiManager.getMouseX(), GameGuiManager.getMouseY());
			if (position.getPossibleMoves().contains(new UTTTCoordinate(coordinate, position.currentBoard))) {
				g.setColor(Color.BLUE);
				int intersectionX = getIntersectionX(GameGuiManager.getMouseX());
				int intersectionY = getIntersectionY(GameGuiManager.getMouseY());

				int snapX = getSquareCornerX(intersectionX);
				int snapY = getSquareCornerY(intersectionY);

				int padding = getSquareWidthFraction(.1);
				g.drawRect(snapX + padding, snapY + padding, round(cellWidth) - 2 * padding, round(cellWidth) - 2 * padding);
			}
		}
	}

	@Override
	public UTTTCoordinate maybeGetUserMove(UserInput input, UltimateTicTacToePosition position) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			if (GuiPlayer.HUMAN.isRequestingMove()) {
				return new UTTTCoordinate(getCoordinate(GameGuiManager.getMouseX(), GameGuiManager.getMouseY()), position.currentBoard);
			}
		}
		return null;
	}

	public int getCenterX(int x) {
		return round(x * cellWidth + cellWidth / 2 + offsetX);
	}

	public int getCenterY(int y) {
		return round(y * cellWidth + cellWidth / 2 + offsetY);
	}

	public Coordinate getCoordinate(int mouseX, int mouseY) {
		return new Coordinate(getIntersectionX(mouseX), getIntersectionY(mouseY));
	}

	public int getIntersectionX(int x) {
		return round((x - offsetX - cellWidth / 2) / cellWidth);
	}

	public int getIntersectionY(int y) {
		return round((y - offsetY - cellWidth / 2) / cellWidth);
	}

	public int getSquareCornerX(int x) {
		return round(x * cellWidth + offsetX);
	}

	public int getSquareCornerY(int y) {
		return round(y * cellWidth + offsetY);
	}

	public int getSquareWidthFraction(double fraction) {
		return round(fraction * cellWidth);
	}
}
