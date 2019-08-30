package bge.igame.player;

import java.awt.Color;

import bge.igame.Coordinate;
import gt.component.IMouseTracker;
import gt.gameentity.GridSizer;
import gt.gameentity.IGraphics;
import gt.util.EMath;

public class GuiPlayerHelper {
    public static Coordinate maybeGetCoordinate(IMouseTracker mouseTracker, GridSizer sizer, int boardWidth) {
        if (GuiPlayer.HUMAN.isRequestingMove()) {
            int x = sizer.getCoordinateX(mouseTracker.mouseX());
            int y = sizer.getCoordinateY(mouseTracker.mouseY());
            if (x >= 0 && x < boardWidth && y >= 0 && y < boardWidth) {
                return Coordinate.valueOf(x, y);
            }
        }
        return null;
    }

    public static void highlightCoordinate(IGraphics g, IMouseTracker mouseTracker, GridSizer sizer, double paddingFraction) {
        highlightCoordinate(g, mouseTracker, sizer, paddingFraction, Color.BLUE);
    }

    public static void highlightCoordinate(IGraphics g, IMouseTracker mouseTracker, GridSizer sizer, double paddingFraction, Color color) {
        g.setColor(color);
        int coordinateX = sizer.getCoordinateX(mouseTracker.mouseX());
        int coordinateY = sizer.getCoordinateY(mouseTracker.mouseY());
        highlightCoordinate(g, sizer, coordinateX, coordinateY, paddingFraction);
    }

    public static void highlightCoordinate(IGraphics g, GridSizer sizer, int coordinateX, int coordinateY, double paddingFraction) {
        int snapX = EMath.round(sizer.getCornerX(coordinateX));
        int snapY = EMath.round(sizer.getCornerY(coordinateY));

        double padding = sizer.cellSize * paddingFraction;
        int x0 = EMath.round(snapX + padding);
        int y0 = EMath.round(snapY + padding);
        int width = EMath.round(sizer.cellSize - 2 * padding);
        g.drawRect(x0, y0, width, width);
    }
}
