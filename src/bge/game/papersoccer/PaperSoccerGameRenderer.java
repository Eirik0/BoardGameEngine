package bge.game.papersoccer;

import java.awt.Color;

import bge.game.papersoccer.PaperSoccerPositionHistory.UndoPaperSoccerMove;
import bge.gui.gamestate.IGameRenderer;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.GuiPlayerHelper;
import bge.igame.player.TwoPlayers;
import gt.component.IMouseTracker;
import gt.gameentity.DrawingMethods;
import gt.gameentity.GridSizer;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;

public class PaperSoccerGameRenderer implements IGameRenderer<Integer, PaperSoccerPosition>, DrawingMethods {
    private static final PaperSoccerPosition INITIAL_POSITION = new PaperSoccerPosition();

    private static final Color P1_COLOR = Color.YELLOW;
    private static final Color P2_COLOR = Color.RED;
    private static final Color FIELD_COLOR = new Color(80, 160, 72);
    private static final Color WALL_COLOR = new Color(139, 69, 19);

    private final IMouseTracker mouseTracker;
    private GridSizer sizer;
    private double ballRadius;

    public PaperSoccerGameRenderer(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, PaperSoccerUtilities.BOARD_WIDTH, PaperSoccerUtilities.BOARD_WIDTH);
        ballRadius = sizer.cellSize / 5;

        g.fillRect(0, 0, imageWidth, imageHeight, new Color(25, 127, 0));
        g.setColor(FIELD_COLOR);
        g.fillRect(sizer.getCenterX(5), sizer.getCenterY(0), sizer.getCenterX(7) - sizer.getCenterX(5), sizer.getCenterY(1) - sizer.getCenterY(0));
        g.fillRect(sizer.getCenterX(2), sizer.getCenterY(1), sizer.getCenterX(10) - sizer.getCenterX(2), sizer.getCenterY(11) - sizer.getCenterY(1));
        g.fillRect(sizer.getCenterX(5), sizer.getCenterY(11), sizer.getCenterX(7) - sizer.getCenterX(5), sizer.getCenterY(12) - sizer.getCenterY(11));

        for (int i = 0; i < PaperSoccerUtilities.BOARD_SIZE; ++i) {
            if (INITIAL_POSITION.board[i] != 0) {
                if (i == 5 || i == 6 || i == 7 || i == 18 || i == 20) {
                    g.setColor(P1_COLOR);
                } else if (i == 161 || i == 162 || i == 163 || i == 148 || i == 150) {
                    g.setColor(P2_COLOR);
                } else {
                    g.setColor(WALL_COLOR);
                }
                drawPaths(g, i, INITIAL_POSITION.board[i], true, 4);
            }
        }

        g.setColor(Color.BLACK);
        g.fillCircle(sizer.getCenterX(5), sizer.getCenterY(0), 2);
        g.fillCircle(sizer.getCenterX(6), sizer.getCenterY(0), 2);
        g.fillCircle(sizer.getCenterX(7), sizer.getCenterY(0), 2);
        g.fillCircle(sizer.getCenterX(5), sizer.getCenterY(12), 2);
        g.fillCircle(sizer.getCenterX(6), sizer.getCenterY(12), 2);
        g.fillCircle(sizer.getCenterX(7), sizer.getCenterY(12), 2);
        g.setColor(Color.WHITE);
        for (int x = 2; x < PaperSoccerUtilities.BOARD_WIDTH - 2; ++x) {
            for (int y = 1; y < PaperSoccerUtilities.BOARD_WIDTH - 1; ++y) {
                g.fillCircle(sizer.getCenterX(x), sizer.getCenterY(y), 2);
            }
        }
    }

    private void drawPaths(IGraphics g, int location, int value, boolean taken, float thickness) {
        Coordinate from = PaperSoccerUtilities.getCoordinate(location);
        int[] directionsTaken = taken ? PaperSoccerUtilities.DIRECTIONS_TAKEN[value] : PaperSoccerUtilities.DIRECTIONS_REMAINING[value];
        for (int i = 0; i < directionsTaken.length; ++i) {
            Coordinate to = PaperSoccerUtilities.getCoordinate(location + directionsTaken[i]);
            double x0 = sizer.getCenterX(from.x);
            double y0 = sizer.getCenterY(from.y);
            double x1 = (x0 + sizer.getCenterX(to.x)) / 2;
            double y1 = (y0 + sizer.getCenterY(to.y)) / 2;
            g.drawThickLine(x0, y0, x1, y1, thickness, true);
        }
    }

    @Override
    public void drawPosition(IGraphics g, PaperSoccerPosition position, MoveList<Integer> possibleMoves, Integer lastMove) {
        drawMoves(g, position);
        drawBall(g, position);
        drawMouse(g, position, possibleMoves);
    }

    private void drawMoves(IGraphics g, PaperSoccerPosition position) {
        Coordinate toCoord = PaperSoccerUtilities.getCoordinate(position.ballLocation);
        int i = position.positionHistory.plyCount;
        while (i > 0) {
            UndoPaperSoccerMove fromMove = position.positionHistory.undoPaperSoccerMoves[i - 1];
            Coordinate fromCoord = PaperSoccerUtilities.getCoordinate(fromMove.ballLocation);
            double percent = Math.pow(Math.E, -(position.positionHistory.plyCount - i + 10) / 50.0);
            g.setColor(DrawingMethods.fadeToColor(Color.WHITE, fromMove.player == TwoPlayers.PLAYER_1 ? P1_COLOR : P2_COLOR, percent));
            g.drawThickLine(sizer.getCenterX(fromCoord.x), sizer.getCenterY(fromCoord.y), sizer.getCenterX(toCoord.x), sizer.getCenterY(toCoord.y), 4, true);
            toCoord = fromCoord;
            --i;
        }
    }

    private void drawBall(IGraphics g, PaperSoccerPosition position) {
        g.setColor(position.currentPlayer == TwoPlayers.PLAYER_1 ? P1_COLOR : P2_COLOR);
        if (!position.gameOver) {
            drawPaths(g, position.ballLocation, position.board[position.ballLocation], false, 2);
        }
        Coordinate ballCoordinate = PaperSoccerUtilities.getCoordinate(position.ballLocation);
        g.fillCircle(sizer.getCenterX(ballCoordinate.x), sizer.getCenterY(ballCoordinate.y), ballRadius);
    }

    private void drawMouse(IGraphics g, PaperSoccerPosition position, MoveList<Integer> possibleMoves) {
        if (mouseTracker.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
            Coordinate mouseCoordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, PaperSoccerUtilities.BOARD_WIDTH);
            if (mouseCoordinate != null && possibleMoves.contains(PaperSoccerUtilities.getMove(mouseCoordinate.x, mouseCoordinate.y))) {
                GuiPlayerHelper.highlightCoordinate(g, mouseTracker, sizer, 0.1, position.currentPlayer == TwoPlayers.PLAYER_1 ? P1_COLOR : P2_COLOR);
            }
        }
    }

    @Override
    public Integer maybeGetUserMove(UserInput input, PaperSoccerPosition position, MoveList<Integer> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, PaperSoccerUtilities.BOARD_WIDTH);
            if (coordinate != null) {
                return PaperSoccerUtilities.getMove(coordinate.x, coordinate.y);
            }
        }
        return null;
    }
}
