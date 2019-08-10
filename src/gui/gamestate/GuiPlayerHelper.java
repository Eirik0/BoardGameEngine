package gui.gamestate;

import java.awt.Color;
import java.awt.Graphics;

import game.Coordinate;
import gui.DrawingMethods;
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

    public static void highlightCoordinate(Graphics g, BoardSizer sizer, double paddingFraction) {
        highlightCoordinate(g, sizer, paddingFraction, Color.BLUE);
    }

    public static void highlightCoordinate(Graphics g, BoardSizer sizer, double paddingFraction, Color color) {
        g.setColor(color);
        int coordinateX = sizer.getCoordinateX(GameGuiManager.getMouseX());
        int coordinateY = sizer.getCoordinateY(GameGuiManager.getMouseY());
        highlightCoordinate(g, sizer, coordinateX, coordinateY, paddingFraction);
    }

    public static void highlightCoordinate(Graphics g, BoardSizer sizer, int coordinateX, int coordinateY, double paddingFraction) {
        int snapX = sizer.getSquareCornerX(coordinateX);
        int snapY = sizer.getSquareCornerY(coordinateY);

        double padding = sizer.cellWidth * paddingFraction;
        g.drawRect(round(snapX + padding), round(snapY + padding), round(sizer.cellWidth - 2 * padding), round(sizer.cellWidth - 2 * padding));
    }

    private static int round(double d) {
        return DrawingMethods.roundS(d);
    }
}
