package bge.game.sudoku;

import java.awt.Color;
import java.awt.Font;

import bge.game.ultimatetictactoe.UltimateTicTacToeGameRenderer;
import bge.gui.gamestate.IGameRenderer;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.gameentity.GridSizer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;
import gt.util.EMath;

public class SudokuGameRenderer implements IGameRenderer<SudokuMove, SudokuPosition>, SudokuConstants {
    private GridSizer sizer;
    private double smallBoardWidth = 0;

    @SuppressWarnings("unused")
    public SudokuGameRenderer(IMouseTracker mouseTracker) {
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, NUM_DIGITS, NUM_DIGITS);

        smallBoardWidth = sizer.gridWidth / 3.0;

        g.fillRect(0, 0, imageWidth, imageHeight, ComponentCreator.backgroundColor());

        g.drawRect(sizer.offsetX, sizer.offsetY, sizer.gridWidth, sizer.gridWidth, ComponentCreator.foregroundColor());

        g.setColor(ComponentCreator.foregroundColor());
        UltimateTicTacToeGameRenderer.drawBoard(g, sizer.offsetX, sizer.offsetY, sizer.gridWidth, 0, 2);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                UltimateTicTacToeGameRenderer.drawBoard(g, sizer.offsetX + smallBoardWidth * x, sizer.offsetY + smallBoardWidth * y, smallBoardWidth, 0, 1);
            }
        }
    }

    @Override
    public void drawPosition(IGraphics g, SudokuPosition position, MoveList<SudokuMove> possibleMoves, SudokuMove lastMove) {
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, EMath.round(sizer.cellSize * 0.33)));
        int i = 0;
        while (i < position.numDecided) {
            int cellIndex = position.decidedCells[i++];
            Coordinate coordinate = SudokuConstants.getCoordinate(cellIndex);
            g.setColor(lastMove != null && cellIndex == lastMove.location ? Color.GREEN : ComponentCreator.foregroundColor());
            g.drawCenteredString(position.cells[cellIndex].toString(), sizer.getCenterX(coordinate.x), sizer.getCenterY(coordinate.y));
        }
    }

    @Override
    public SudokuMove maybeGetUserMove(UserInput input, SudokuPosition position, MoveList<SudokuMove> possibleMoves) {
        return null; // Currently only computer player
    }
}
