package bge.gui.gamestate;

import bge.gui.DrawingMethods;

public class BoardSizer implements DrawingMethods {
    public final int boardWidth;
    public final double cellWidth;
    public final double offsetX;
    public final double offsetY;

    public BoardSizer(int imageWidth, int imageHeight, int numCells) {
        boardWidth = Math.min(imageWidth, imageHeight);
        offsetX = (imageWidth - boardWidth) / 2.0;
        offsetY = (imageHeight - boardWidth) / 2.0;
        cellWidth = (double) boardWidth / numCells;
    }

    public int getSquareCornerX(int x) { // Upper left
        return round(x * cellWidth + offsetX);
    }

    public int getSquareCornerY(int y) { // Upper left
        return round(y * cellWidth + offsetY);
    }

    public int getCenterX(int cellX) {
        return round(cellX * cellWidth + cellWidth / 2 + offsetX);
    }

    public int getCenterY(int cellY) {
        return round(cellY * cellWidth + cellWidth / 2 + offsetY);
    }

    public int getCoordinateX(int mouseX) {
        return round((mouseX - offsetX - cellWidth / 2) / cellWidth);
    }

    public int getCoordinateY(int mouseY) {
        return round((mouseY - offsetY - cellWidth / 2) / cellWidth);
    }
}
