package game.gomoku;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;
import gui.GameGuiManager;
import gui.gamestate.BoardSizer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.GuiPlayerHelper;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class GomokuGameRenderer implements IGameRenderer<Integer, GomokuPosition> {
	private static final Color BOARD_COLOR = new Color(155, 111, 111);
	private static final Coordinate[] STAR_POINTS = new Coordinate[] {
			Coordinate.valueOf(3, 3), Coordinate.valueOf(9, 3), Coordinate.valueOf(15, 3),
			Coordinate.valueOf(3, 9), Coordinate.valueOf(9, 9), Coordinate.valueOf(15, 9),
			Coordinate.valueOf(3, 15), Coordinate.valueOf(9, 15), Coordinate.valueOf(15, 15)
	};

	private BoardSizer sizer;

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int imageWidth = GameGuiManager.getComponentWidth();
		int imageHeight = GameGuiManager.getComponentHeight();

		sizer = new BoardSizer(imageWidth, imageHeight, GomokuUtilities.BOARD_WIDTH);

		fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

		fillRect(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, sizer.boardWidth, BOARD_COLOR);

		g.setColor(Color.BLACK);
		// Bounds & Grid
		for (int i = 0; i < GomokuUtilities.BOARD_WIDTH; ++i) {
			g.drawLine(sizer.getCenterX(i), sizer.getCenterY(0), sizer.getCenterX(i), sizer.getCenterY(GomokuUtilities.BOARD_WIDTH - 1));
			g.drawLine(sizer.getCenterX(0), sizer.getCenterY(i), sizer.getCenterX(GomokuUtilities.BOARD_WIDTH - 1), sizer.getCenterY(i));
		}
		// Small Circles
		double small = Math.min(2, sizer.boardWidth / 200.0);
		for (int x = 0; x < GomokuUtilities.BOARD_WIDTH; ++x) {
			for (int y = 0; y < GomokuUtilities.BOARD_WIDTH; ++y) {
				fillCircle(g, sizer.getCenterX(x), sizer.getCenterY(y), small);
			}
		}
		// Large
		double large = Math.min(4, sizer.boardWidth / 100.0);
		for (Coordinate starPoint : STAR_POINTS) {
			fillCircle(g, sizer.getCenterX(starPoint.x), sizer.getCenterY(starPoint.y), large);
		}
	}

	@Override
	public void drawPosition(Graphics2D g, GomokuPosition position, MoveList<Integer> possibleMoves, Integer lastMove) {
		drawMoves(g, position, lastMove);
		drawMouseOn(g, possibleMoves);
	}

	private void drawMoves(Graphics2D g, GomokuPosition position, Integer lastMove) {
		for (int y = 0; y < GomokuUtilities.BOARD_WIDTH; y++) {
			for (int x = 0; x < GomokuUtilities.BOARD_WIDTH; x++) {
				int move = GomokuUtilities.getMove(x, y).intValue();
				if (position.board[move] != TwoPlayers.UNPLAYED) {
					Color color = position.board[move] == TwoPlayers.PLAYER_1 ? Color.BLACK : Color.WHITE;
					g.setColor(color);
					fillCircle(g, sizer.getCenterX(x), sizer.getCenterY(y), sizer.cellWidth * 0.45);
				}
			}
		}
		if (lastMove != null) {
			g.setColor(Color.RED);
			Coordinate lastMoveCoord = GomokuUtilities.MOVE_COORDS[lastMove.intValue()];
			drawCircle(g, sizer.getCenterX(lastMoveCoord.y), sizer.getCenterY(lastMoveCoord.x), sizer.cellWidth * 0.225);
		}
	}

	private void drawMouseOn(Graphics g, MoveList<Integer> possibleMoves) {
		if (GameGuiManager.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, GomokuUtilities.BOARD_WIDTH);
			if (coordinate != null && possibleMoves.contains(GomokuUtilities.getMove(coordinate.x, coordinate.y))) {
				GuiPlayerHelper.highlightCoordinate(g, sizer, 0.1);
			}
		}
	}

	@Override
	public Integer maybeGetUserMove(UserInput input, GomokuPosition position, MoveList<Integer> possibleMoves) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, GomokuUtilities.BOARD_WIDTH);
			if (coordinate != null) {
				return GomokuUtilities.getMove(coordinate.x, coordinate.y);
			}
		}
		return null;
	}
}
