package bge.gui.gamestate;

import java.awt.Color;
import java.awt.Graphics;

import bge.game.Coordinate;
import bge.gui.DrawingMethods;
import bge.gui.GameGuiManager;
import bge.gui.GuiPlayer;
import gt.gameentity.GridSizer;

public class GuiPlayerHelper {
    public static Coordinate maybeGetCoordinate(GridSizer sizer, int boardWidth) {
        if (GuiPlayer.HUMAN.isRequestingMove()) {
            int x = sizer.getCoordinateX(GameGuiManager.getMouseX());
            int y = sizer.getCoordinateY(GameGuiManager.getMouseY());
            if (x >= 0 && x < boardWidth && y >= 0 && y < boardWidth) {
                return Coordinate.valueOf(x, y);
            }
        }
        return null;
    }

    public static void highlightCoordinate(Graphics g, GridSizer sizer, double paddingFraction) {
        highlightCoordinate(g, sizer, paddingFraction, Color.BLUE);
    }

    public static void highlightCoordinate(Graphics g, GridSizer sizer, double paddingFraction, Color color) {
        g.setColor(color);
        int coordinateX = sizer.getCoordinateX(GameGuiManager.getMouseX());
        int coordinateY = sizer.getCoordinateY(GameGuiManager.getMouseY());
        highlightCoordinate(g, sizer, coordinateX, coordinateY, paddingFraction);
    }

    public static void highlightCoordinate(Graphics g, GridSizer sizer, int coordinateX, int coordinateY, double paddingFraction) {
        int snapX = round(sizer.getCornerX(coordinateX));
        int snapY = round(sizer.getCornerY(coordinateY));

        double padding = sizer.cellSize * paddingFraction;
        g.drawRect(round(snapX + padding), round(snapY + padding), round(sizer.cellSize - 2 * padding), round(sizer.cellSize - 2 * padding));
    }

    private static int round(double d) {
        return DrawingMethods.roundS(d);
    }
}
