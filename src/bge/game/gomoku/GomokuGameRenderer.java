package bge.game.gomoku;

import java.awt.Color;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.GuiPlayerHelper;
import bge.igame.player.TwoPlayers;
import bge.main.BoardGameEngineMain;
import gt.component.IMouseTracker;
import gt.gameentity.GridSizer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;

public class GomokuGameRenderer implements IGameRenderer<Integer, GomokuPosition> {
    private static final Color BOARD_COLOR = new Color(155, 111, 111);
    private static final Coordinate[] STAR_POINTS = new Coordinate[] {
            Coordinate.valueOf(3, 3), Coordinate.valueOf(9, 3), Coordinate.valueOf(15, 3),
            Coordinate.valueOf(3, 9), Coordinate.valueOf(9, 9), Coordinate.valueOf(15, 9),
            Coordinate.valueOf(3, 15), Coordinate.valueOf(9, 15), Coordinate.valueOf(15, 15)
    };

    private final IMouseTracker mouseTracker;
    private GridSizer sizer;

    public GomokuGameRenderer(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, GomokuUtilities.BOARD_WIDTH, GomokuUtilities.BOARD_WIDTH);

        g.fillRect(0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

        g.fillRect(sizer.offsetX, sizer.offsetY, sizer.gridWidth, sizer.gridHeight, BOARD_COLOR);

        g.setColor(Color.BLACK);
        // Bounds & Grid
        for (int i = 0; i < GomokuUtilities.BOARD_WIDTH; ++i) {
            g.drawLine(sizer.getCenterX(i), sizer.getCenterY(0), sizer.getCenterX(i), sizer.getCenterY(GomokuUtilities.BOARD_WIDTH - 1));
            g.drawLine(sizer.getCenterX(0), sizer.getCenterY(i), sizer.getCenterX(GomokuUtilities.BOARD_WIDTH - 1), sizer.getCenterY(i));
        }
        // Small Circles
        double small = Math.min(2, sizer.gridWidth / 200.0);
        for (int x = 0; x < GomokuUtilities.BOARD_WIDTH; ++x) {
            for (int y = 0; y < GomokuUtilities.BOARD_WIDTH; ++y) {
                g.fillCircle(sizer.getCenterX(x), sizer.getCenterY(y), small);
            }
        }
        // Large
        double large = Math.min(4, sizer.gridWidth / 100.0);
        for (Coordinate starPoint : STAR_POINTS) {
            g.fillCircle(sizer.getCenterX(starPoint.x), sizer.getCenterY(starPoint.y), large);
        }
    }

    @Override
    public void drawPosition(IGraphics g, GomokuPosition position, MoveList<Integer> possibleMoves, Integer lastMove) {
        drawMoves(g, position, lastMove);
        drawMouseOn(g, possibleMoves);
    }

    private void drawMoves(IGraphics g, GomokuPosition position, Integer lastMove) {
        for (int y = 0; y < GomokuUtilities.BOARD_WIDTH; y++) {
            for (int x = 0; x < GomokuUtilities.BOARD_WIDTH; x++) {
                int move = GomokuUtilities.getMove(x, y).intValue();
                if (position.board[move] != TwoPlayers.UNPLAYED) {
                    Color color = position.board[move] == TwoPlayers.PLAYER_1 ? Color.BLACK : Color.WHITE;
                    g.setColor(color);
                    g.fillCircle(sizer.getCenterX(x), sizer.getCenterY(y), sizer.cellSize * 0.45);
                }
            }
        }
        if (lastMove != null) {
            g.setColor(Color.RED);
            Coordinate lastMoveCoord = GomokuUtilities.MOVE_COORDS[lastMove.intValue()];
            g.drawCircle(sizer.getCenterX(lastMoveCoord.y), sizer.getCenterY(lastMoveCoord.x), sizer.cellSize * 0.225);
        }
    }

    private void drawMouseOn(IGraphics g, MoveList<Integer> possibleMoves) {
        if (mouseTracker.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, GomokuUtilities.BOARD_WIDTH);
            if (coordinate != null && possibleMoves.contains(GomokuUtilities.getMove(coordinate.x, coordinate.y))) {
                GuiPlayerHelper.highlightCoordinate(g, mouseTracker, sizer, 0.1);
            }
        }
    }

    @Override
    public Integer maybeGetUserMove(UserInput input, GomokuPosition position, MoveList<Integer> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, GomokuUtilities.BOARD_WIDTH);
            if (coordinate != null) {
                return GomokuUtilities.getMove(coordinate.x, coordinate.y);
            }
        }
        return null;
    }
}
