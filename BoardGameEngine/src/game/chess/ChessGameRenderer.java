package game.chess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import game.Coordinate;
import gui.GameGuiManager;
import gui.gamestate.BoardSizer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.GuiPlayerHelper;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class ChessGameRenderer implements IGameRenderer<ChessMove, ChessPosition>, ChessConstants {
	private final ChessPieceImages pieceImages;

	private BoardSizer sizer;

	private Coordinate movingPieceStart;

	public ChessGameRenderer() {
		pieceImages = ChessPieceImages.getInstance();
	}

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int imageWidth = GameGuiManager.getComponentWidth();
		int imageHeight = GameGuiManager.getComponentHeight();

		sizer = new BoardSizer(imageWidth, imageHeight, BOARD_WIDTH);

		fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

		boolean white = true;
		for (int x = 0; x < BOARD_WIDTH; ++x) {
			for (int y = 0; y < BOARD_WIDTH; ++y) {
				Color color = white ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR;
				fillRect(g, sizer.offsetX + sizer.cellWidth * x, sizer.offsetY + sizer.cellWidth * y, sizer.cellWidth + 1, sizer.cellWidth + 1, color);
				white = !white;
			}
			white = !white;
		}
	}

	@Override
	public void drawPosition(Graphics2D g, ChessPosition position, List<ChessMove> possibleMoves, ChessMove lastMove) {
		drawBoard(g, position);
		drawMouseOn(g, position, possibleMoves);
	}

	private void drawBoard(Graphics2D g, ChessPosition position) {
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (movingPieceStart != null && movingPieceStart.x == x && movingPieceStart.y == y) {
					continue;
				}
				int piece = position.cells[y][x];
				BufferedImage pieceImage = pieceImages.getPieceImage(piece);
				if (pieceImage != null) {
					int x0 = round(sizer.offsetX + sizer.cellWidth * x);
					int y0 = round(sizer.offsetY + sizer.cellWidth * y);
					int width = round(sizer.cellWidth + 1);
					g.drawImage(pieceImage, x0, y0, width, width, null);
				}
			}
		}
	}

	private void drawMouseOn(Graphics g, ChessPosition position, List<ChessMove> possibleMoves) {
		if (GameGuiManager.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
			Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
			if (coordinate != null) {
				GuiPlayerHelper.highlightCoordinate(g, sizer, 1.0 / 32);
			}
			if (movingPieceStart != null) {
				BufferedImage pieceImage = pieceImages.getPieceImage(position.cells[movingPieceStart.y][movingPieceStart.x]);
				double width = sizer.cellWidth + 1;
				int x0 = round(GameGuiManager.getMouseX() - width / 2);
				int y0 = round(GameGuiManager.getMouseY() - width / 2);
				g.drawImage(pieceImage, x0, y0, round(width), round(width), null);
			}
		}
	}

	@Override
	public ChessMove maybeGetUserMove(UserInput input, ChessPosition position) {
		if (input == UserInput.LEFT_BUTTON_RELEASED) {
			if (movingPieceStart != null) {
				Coordinate from = movingPieceStart;
				movingPieceStart = null;
				Coordinate to = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
				if (to != null) {
					return new ChessMove(from, to, position);
				}
			}
		} else if (input == UserInput.LEFT_BUTTON_PRESSED) {
			Coordinate from = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
			if (from != null && position.cells[from.y][from.x] != UNPLAYED) {
				movingPieceStart = from;
			}
		}
		return null;
	}
}
