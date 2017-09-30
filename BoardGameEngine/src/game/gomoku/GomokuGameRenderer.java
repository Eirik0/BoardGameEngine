package game.gomoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;

import game.Coordinate;
import game.TwoPlayers;
import gui.GameGuiManager;
import gui.gamestate.BoardSizer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.GuiPlayerHelper;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class GomokuGameRenderer implements IGameRenderer<Coordinate, GomokuPosition> {
	private static final Color BOARD_COLOR = new Color(155, 111, 111);

	private BoardSizer sizer;

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int imageWidth = GameGuiManager.getComponentWidth();
		int imageHeight = GameGuiManager.getComponentHeight();

		sizer = new BoardSizer(imageWidth, imageHeight, GomokuPosition.BOARD_WIDTH);

		fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

		fillRect(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, sizer.boardWidth, BOARD_COLOR);

		g.setColor(Color.BLACK);
		// Bounds & Grid
		for (int i = 0; i < GomokuPosition.BOARD_WIDTH; ++i) {
			g.drawLine(sizer.getCenterX(i), sizer.getCenterY(0), sizer.getCenterX(i), sizer.getCenterY(GomokuPosition.BOARD_WIDTH - 1));
			g.drawLine(sizer.getCenterX(0), sizer.getCenterY(i), sizer.getCenterX(GomokuPosition.BOARD_WIDTH - 1), sizer.getCenterY(i));
		}
		// Small Circles
		double small = Math.min(2, sizer.boardWidth / 200.0);
		for (int x = 0; x < GomokuPosition.BOARD_WIDTH; ++x) {
			for (int y = 0; y < GomokuPosition.BOARD_WIDTH; ++y) {
				fillCircle(g, sizer.getCenterX(x), sizer.getCenterY(y), small);
				g.fillOval(round(-small), round(-small), round(2 * small), round(2 * small));
			}
		}
		// Large
		double large = Math.min(4, sizer.boardWidth / 100.0);
		for (Coordinate starPoint : getStarPoints()) {
			fillCircle(g, sizer.getCenterX(starPoint.x), sizer.getCenterY(starPoint.y), large);
		}
	}

	private List<Coordinate> getStarPoints() {
		return Arrays.asList(Coordinate.valueOf(3, 3), Coordinate.valueOf(9, 3), Coordinate.valueOf(15, 3),
				Coordinate.valueOf(3, 9), Coordinate.valueOf(9, 9), Coordinate.valueOf(15, 9),
				Coordinate.valueOf(3, 15), Coordinate.valueOf(9, 15), Coordinate.valueOf(15, 15));
	}

	@Override
	public void drawPosition(Graphics2D g, GomokuPosition position, List<Coordinate> possibleMoves, Coordinate lastMove) {
		drawMoves(g, position, lastMove);
		drawMouseOn(g, possibleMoves);
	}

	private void drawMoves(Graphics2D g, GomokuPosition position, Coordinate lastMove) {
		for (int y = 0; y < GomokuPosition.BOARD_WIDTH; y++) {
			int[] row = position.board[y];
			for (int x = 0; x < GomokuPosition.BOARD_WIDTH; x++) {
				if (row[x] != TwoPlayers.UNPLAYED) {
					Color color = row[x] == TwoPlayers.PLAYER_1 ? Color.WHITE : Color.BLACK;
					g.setColor(color);
					fillCircle(g, sizer.getCenterX(x), sizer.getCenterY(y), sizer.cellWidth * 0.45);
				}
			}
		}
		if (lastMove != null) {
			g.setColor(Color.RED);
			drawCircle(g, sizer.getCenterX(lastMove.x), sizer.getCenterY(lastMove.y), sizer.cellWidth * 0.225);
		}
	}

	private void drawMouseOn(Graphics g, List<Coordinate> possibleMoves) {
		if (GameGuiManager.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, GomokuPosition.BOARD_WIDTH);
			if (coordinate != null && possibleMoves.contains(coordinate)) {
				GuiPlayerHelper.highlightCoordinate(g, sizer, 0.1);
			}
		}
	}

	@Override
	public Coordinate maybeGetUserMove(UserInput input, GomokuPosition position, List<Coordinate> possibleMoves) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			return GuiPlayerHelper.maybeGetCoordinate(sizer, GomokuPosition.BOARD_WIDTH);
		}
		return null;
	}
}
