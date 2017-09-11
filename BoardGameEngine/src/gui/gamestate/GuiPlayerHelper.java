package gui.gamestate;

import java.awt.Color;
import java.awt.Graphics;

import game.Coordinate;
import gui.GameGuiManager;
import gui.GuiPlayer;

public class GuiPlayerHelper {
	public static Coordinate maybeGetCoordinate(BoardSizer sizer, int boardWidth) {
		if (GuiPlayer.HUMAN.isRequestingMove()) {
			int x = sizer.getCoordinateX(GameGuiManager.getMouseX());
			int y = sizer.getCoordinateY(GameGuiManager.getMouseY());
			if (x >= 0 && x < boardWidth && y >= 0 && y < boardWidth) {
				return Coordinate.valueOf(x, y);
			}
		}
		return null;
	}

	public static void highlightCoordinate(Graphics g, BoardSizer sizer) {
		g.setColor(Color.BLUE);
		int intersectionX = sizer.getCoordinateX(GameGuiManager.getMouseX());
		int intersectionY = sizer.getCoordinateY(GameGuiManager.getMouseY());

		int snapX = sizer.getSquareCornerX(intersectionX);
		int snapY = sizer.getSquareCornerY(intersectionY);

		double padding = sizer.cellWidth * 0.1;
		g.drawRect(round(snapX + padding), round(snapY + padding), round(sizer.cellWidth - 2 * padding), round(sizer.cellWidth - 2 * padding));
	}

	private static int round(double d) {
		return (int) Math.round(d);
	}
}
