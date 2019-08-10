package game.sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import game.Coordinate;
import game.MoveList;
import game.ultimatetictactoe.UltimateTicTacToeGameRenderer;
import gui.GameGuiManager;
import gui.gamestate.BoardSizer;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class SudokuGameRenderer implements IGameRenderer<SudokuMove, SudokuPosition>, SudokuConstants {
    private BoardSizer sizer;
    private double smallBoardWidth = 0;

    @Override
    public void initializeAndDrawBoard(Graphics2D g) {
        int imageWidth = GameGuiManager.getComponentWidth();
        int imageHeight = GameGuiManager.getComponentHeight();

        sizer = new BoardSizer(imageWidth, imageHeight, NUM_DIGITS);

        smallBoardWidth = sizer.boardWidth / 3.0;

        fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

        drawRect(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, sizer.boardWidth, BoardGameEngineMain.FOREGROUND_COLOR);

        g.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
        UltimateTicTacToeGameRenderer.drawBoard(g, sizer.offsetX, sizer.offsetY, sizer.boardWidth, 0, 2);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                UltimateTicTacToeGameRenderer.drawBoard(g, sizer.offsetX + smallBoardWidth * x, sizer.offsetY + smallBoardWidth * y, smallBoardWidth, 0, 1);
            }
        }
    }

    @Override
    public void drawPosition(Graphics2D g, SudokuPosition position, MoveList<SudokuMove> possibleMoves, SudokuMove lastMove) {
        Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, round(sizer.cellWidth * 0.33));
        int i = 0;
        while (i < position.numDecided) {
            int cellIndex = position.decidedCells[i++];
            Coordinate coordinate = SudokuConstants.getCoordinate(cellIndex);
            g.setColor(lastMove != null && cellIndex == lastMove.location ? Color.GREEN : BoardGameEngineMain.FOREGROUND_COLOR);
            drawCenteredString(g, smallFont, position.cells[cellIndex].toString(), sizer.getCenterX(coordinate.x), sizer.getCenterY(coordinate.y));
        }
    }

    @Override
    public SudokuMove maybeGetUserMove(UserInput input, SudokuPosition position, MoveList<SudokuMove> possibleMoves) {
        return null; // Currently only computer player
    }
}
