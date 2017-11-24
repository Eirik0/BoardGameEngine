package game.ultimatetictactoe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;
import gui.DrawingMethods;
import gui.GameGuiManager;
import gui.gamestate.BoardSizer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.GuiPlayerHelper;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class UltimateTicTacToeGameRenderer implements IGameRenderer<Coordinate, UltimateTicTacToePosition> {
	private static final Color WOOD_COLOR = new Color(166, 128, 100);

	private BoardSizer sizer;
	private double smallBoardWidth = 0;

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int imageWidth = GameGuiManager.getComponentWidth();
		int imageHeight = GameGuiManager.getComponentHeight();

		sizer = new BoardSizer(imageWidth, imageHeight, UltimateTicTacToePosition.BOARD_WIDTH);

		smallBoardWidth = sizer.boardWidth / 3.0;

		fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

		fillRect(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, sizer.boardWidth, WOOD_COLOR);

		g.setColor(Color.BLACK);
		drawBoard(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, sizer.cellWidth * 0.05, 4);

		for (int x = 0; x < 3; ++x) {
			for (int y = 0; y < 3; ++y) {
				drawBoard(g, sizer.offsetX + smallBoardWidth * x, sizer.offsetY + smallBoardWidth * y, smallBoardWidth, sizer.cellWidth * 0.1, 3);
			}
		}
	}

	public static void drawBoard(Graphics g, double x0, double y0, double width, double padding, int lineThickness) {
		drawThickVLike(g, x0 + width / 3, y0 + padding, y0 + width - padding, lineThickness);
		drawThickVLike(g, x0 + 2 * width / 3, y0 + padding, y0 + width - padding, lineThickness);
		drawThickHLike(g, y0 + width / 3, x0 + padding, x0 + width - padding, lineThickness);
		drawThickHLike(g, y0 + 2 * width / 3, x0 + padding, x0 + width - padding, lineThickness);
	}

	private static void drawThickHLike(Graphics g, double y, double x0, double x1, double thickness) {
		g.fillRect(DrawingMethods.roundS(x0), DrawingMethods.roundS(y - thickness / 2), DrawingMethods.roundS(x1 - x0), DrawingMethods.roundS(thickness));
	}

	private static void drawThickVLike(Graphics g, double x, double y0, double y1, double thickness) {
		g.fillRect(DrawingMethods.roundS(x - thickness / 2), DrawingMethods.roundS(y0), DrawingMethods.roundS(thickness), DrawingMethods.roundS(y1 - y0));
	}

	@Override
	public void drawPosition(Graphics2D g, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves, Coordinate lastMove) {
		drawMoves(g, position, lastMove);
		highlightBoardInPlay(g, position, possibleMoves);
		drawMouseOn(g, position, possibleMoves);
	}

	private void drawMoves(Graphics2D g, UltimateTicTacToePosition position, Coordinate lastMove) {
		Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, round(sizer.cellWidth * 0.33));
		for (int n = 0; n < UltimateTicTacToePosition.BOARD_WIDTH; ++n) {
			for (int m = 0; m < UltimateTicTacToePosition.BOARD_WIDTH; ++m) {
				int playerInt = (position.boards[n] >> (m << 1)) & TwoPlayers.BOTH_PLAYERS;
				if (playerInt != TwoPlayers.UNPLAYED) {
					Coordinate boardXY = UltimateTicTacToeUtilities.getBoardXY(n, m);
					g.setColor(lastMove != null && n == lastMove.x && m == lastMove.y ? Color.WHITE : Color.BLACK);
					String player = playerInt == TwoPlayers.PLAYER_1 ? "X" : "O";
					drawCenteredString(g, smallFont, player, sizer.getCenterX(boardXY.x), sizer.getCenterY(boardXY.y));
				}
			}
		}
		Font largeFont = new Font(Font.SANS_SERIF, Font.PLAIN, round(sizer.cellWidth * 3));
		for (int m = 0; m < UltimateTicTacToePosition.BOARD_WIDTH; ++m) {
			int wonBoardsInt = (position.wonBoards >> (m << 1)) & TwoPlayers.BOTH_PLAYERS;
			if (wonBoardsInt != TwoPlayers.UNPLAYED) {
				g.setColor(lastMove != null && lastMove.x == m ? Color.WHITE : Color.BLACK);
				String player = wonBoardsInt == TwoPlayers.PLAYER_1 ? "X" : "O";
				Coordinate intersection = UltimateTicTacToeUtilities.getBoardXY(m, 4); // 4 = the center square of that board
				drawCenteredString(g, largeFont, player, sizer.getCenterX(intersection.x), sizer.getCenterY(intersection.y));
			}
		}
	}

	private void highlightBoardInPlay(Graphics2D g, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
		if (possibleMoves.size() == 0) {
			return;
		}
		// draw the current board in play
		g.setColor(Color.BLUE);
		double padding = sizer.cellWidth * 0.05;
		if (position.currentBoard == UltimateTicTacToePosition.ANY_BOARD) {
			int highlightWidth = round(sizer.boardWidth - 1.5 * padding);
			g.drawRect(round(sizer.offsetX + padding), round(sizer.offsetY + padding), highlightWidth, highlightWidth);
		} else {
			Coordinate boardXY = UltimateTicTacToeUtilities.getBoardXY(position.currentBoard, 0); // 0 = upper left square
			int highlightWidth = round(smallBoardWidth - 2 * padding);
			g.drawRect(round(sizer.getSquareCornerX(boardXY.x) + padding), round(sizer.getSquareCornerY(boardXY.y) + padding), highlightWidth, highlightWidth);
		}
	}

	private void drawMouseOn(Graphics g, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
		if (GameGuiManager.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, UltimateTicTacToePosition.BOARD_WIDTH);
			if (coordinate != null) {
				Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(coordinate.x, coordinate.y);
				if (possibleMoves.contains(boardNM)) {
					GuiPlayerHelper.highlightCoordinate(g, sizer, 0.1);
				}
			}
		}
	}

	@Override
	public Coordinate maybeGetUserMove(UserInput input, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, UltimateTicTacToePosition.BOARD_WIDTH);
			if (coordinate != null) {
				return UltimateTicTacToeUtilities.getBoardNM(coordinate.x, coordinate.y);
			}
		}
		return null;
	}
}
