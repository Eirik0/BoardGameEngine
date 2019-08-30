package bge.game.ultimatetictactoe;

import java.awt.Color;
import java.awt.Font;

import bge.game.tictactoe.TicTacToeUtilities;
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
import gt.util.EMath;

public class UltimateTicTacToeGameRenderer implements IGameRenderer<Coordinate, UltimateTicTacToePosition> {
    private static final Coordinate[] BOARD_NM = new Coordinate[] {
            nm(0, 0), nm(0, 1), nm(0, 2), nm(1, 0), nm(1, 1), nm(1, 2), nm(2, 0), nm(2, 1), nm(2, 2),
            nm(0, 3), nm(0, 4), nm(0, 5), nm(1, 3), nm(1, 4), nm(1, 5), nm(2, 3), nm(2, 4), nm(2, 5),
            nm(0, 6), nm(0, 7), nm(0, 8), nm(1, 6), nm(1, 7), nm(1, 8), nm(2, 6), nm(2, 7), nm(2, 8),
            nm(3, 0), nm(3, 1), nm(3, 2), nm(4, 0), nm(4, 1), nm(4, 2), nm(5, 0), nm(5, 1), nm(5, 2),
            nm(3, 3), nm(3, 4), nm(3, 5), nm(4, 3), nm(4, 4), nm(4, 5), nm(5, 3), nm(5, 4), nm(5, 5),
            nm(3, 6), nm(3, 7), nm(3, 8), nm(4, 6), nm(4, 7), nm(4, 8), nm(5, 6), nm(5, 7), nm(5, 8),
            nm(6, 0), nm(6, 1), nm(6, 2), nm(7, 0), nm(7, 1), nm(7, 2), nm(8, 0), nm(8, 1), nm(8, 2),
            nm(6, 3), nm(6, 4), nm(6, 5), nm(7, 3), nm(7, 4), nm(7, 5), nm(8, 3), nm(8, 4), nm(8, 5),
            nm(6, 6), nm(6, 7), nm(6, 8), nm(7, 6), nm(7, 7), nm(7, 8), nm(8, 6), nm(8, 7), nm(8, 8) };

    private static Coordinate nm(int n, int m) {
        return Coordinate.valueOf(n, m);
    }

    public static Coordinate getBoardXY(int n, int m) {
        Coordinate intersection = BOARD_NM[n * UltimateTicTacToePosition.BOARD_WIDTH + m];
        return Coordinate.valueOf(intersection.y, intersection.x);
    }

    public static Coordinate getBoardNM(int x, int y) {
        return BOARD_NM[y * UltimateTicTacToePosition.BOARD_WIDTH + x];
    }

    private static final Color WOOD_COLOR = new Color(206, 168, 140);

    private final IMouseTracker mouseTracker;
    private GridSizer sizer;
    private double smallBoardWidth;
    private Font smallFont;
    private Font largeFont;

    public UltimateTicTacToeGameRenderer(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        sizer = new GridSizer(imageWidth, imageHeight, UltimateTicTacToePosition.BOARD_WIDTH, UltimateTicTacToePosition.BOARD_WIDTH);
        smallBoardWidth = sizer.gridWidth / 3.0;
        smallFont = new Font(Font.MONOSPACED, Font.BOLD, EMath.round(sizer.cellSize * 0.75));
        largeFont = new Font(Font.MONOSPACED, Font.BOLD, EMath.round(sizer.cellSize * 4));

        g.fillRect(0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

        g.fillRect(sizer.offsetX, sizer.offsetY, sizer.gridWidth, sizer.gridHeight, WOOD_COLOR);
    }

    public static void drawBoard(IGraphics g, double x0, double y0, double width, double padding, int lineThickness) {
        drawThickVLike(g, x0 + width / 3, y0 + padding, y0 + width - padding, lineThickness);
        drawThickVLike(g, x0 + 2 * width / 3, y0 + padding, y0 + width - padding, lineThickness);
        drawThickHLike(g, y0 + width / 3, x0 + padding, x0 + width - padding, lineThickness);
        drawThickHLike(g, y0 + 2 * width / 3, x0 + padding, x0 + width - padding, lineThickness);
    }

    private static void drawThickHLike(IGraphics g, double y, double x0, double x1, double thickness) {
        g.fillRect(x0, y - thickness / 2, x1 - x0, thickness);
    }

    private static void drawThickVLike(IGraphics g, double x, double y0, double y1, double thickness) {
        g.fillRect(x - thickness / 2, y0, thickness, y1 - y0);
    }

    @Override
    public void drawPosition(IGraphics g, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves, Coordinate lastMove) {
        highlightBoardInPlay(g, position, possibleMoves);
        drawBoards(g);
        drawMoves(g, position, lastMove);
        drawMouseOn(g, possibleMoves);
    }

    private void highlightBoardInPlay(IGraphics g, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
        if (possibleMoves.size() == 0) {
            return;
        }
        // draw the current board in play
        g.setColor(getHighlightColor(position.getCurrentPlayer()));
        if (position.currentBoard == UltimateTicTacToePosition.ANY_BOARD) {
            int n = 0;
            do {
                if (((position.wonBoards | position.fullBoards) & TicTacToeUtilities.POS[n]) == TwoPlayers.UNPLAYED) {
                    highlightBoard(g, n);
                }
            } while (++n < UltimateTicTacToePosition.BOARD_WIDTH);
        } else {
            highlightBoard(g, position.currentBoard);
        }
    }

    private void highlightBoard(IGraphics g, int board) {
        Coordinate boardXY = getBoardXY(board, 0); // 0 = upper left square
        double highlightWidth = smallBoardWidth - 2;
        g.fillRect(sizer.getCornerX(boardXY.x), sizer.getCornerY(boardXY.y), highlightWidth, highlightWidth);
    }

    public Color getHighlightColor(int currentPlayer) {
        return currentPlayer == TwoPlayers.PLAYER_1 ? new Color(220, 220, 255) : new Color(255, 220, 220);
    }

    private void drawBoards(IGraphics g) {
        g.setColor(Color.BLACK);
        drawBoard(g, sizer.offsetX, sizer.offsetY, sizer.gridWidth, sizer.cellSize * 0.05, 4);

        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                drawBoard(g, sizer.offsetX + smallBoardWidth * x, sizer.offsetY + smallBoardWidth * y, smallBoardWidth, sizer.cellSize * 0.1, 3);
            }
        }
    }

    private void drawMoves(IGraphics g, UltimateTicTacToePosition position, Coordinate lastMove) {
        for (int n = 0; n < UltimateTicTacToePosition.BOARD_WIDTH; ++n) {
            for (int m = 0; m < UltimateTicTacToePosition.BOARD_WIDTH; ++m) {
                int playerInt = (position.boards[n] >> (m << 1)) & TwoPlayers.BOTH_PLAYERS;
                if (playerInt != TwoPlayers.UNPLAYED) {
                    Coordinate boardXY = getBoardXY(n, m);
                    g.setColor(getPlayerColor(playerInt, lastMove != null && n == lastMove.x && m == lastMove.y));
                    String player = playerInt == TwoPlayers.PLAYER_1 ? "X" : "O";
                    g.setFont(smallFont);
                    g.drawCenteredString(player, sizer.getCenterX(boardXY.x), sizer.getCenterY(boardXY.y));
                }
            }
        }
        for (int m = 0; m < UltimateTicTacToePosition.BOARD_WIDTH; ++m) {
            int wonBoardsInt = (position.wonBoards >> (m << 1)) & TwoPlayers.BOTH_PLAYERS;
            if (wonBoardsInt != TwoPlayers.UNPLAYED) {
                String player = wonBoardsInt == TwoPlayers.PLAYER_1 ? "X" : "O";
                g.setColor(getPlayerColor(wonBoardsInt, lastMove != null && lastMove.x == m));
                Coordinate intersection = getBoardXY(m, 4); // 4 = the center square of that board
                g.setFont(largeFont);
                g.drawCenteredString(player, sizer.getCenterX(intersection.x), sizer.getCenterY(intersection.y));
            }
        }
    }

    private static Color getPlayerColor(int player, boolean lastMove) {
        Color color = player == TwoPlayers.PLAYER_1 ? Color.BLUE : Color.RED;
        return lastMove ? color.darker().darker() : color;
    }

    private void drawMouseOn(IGraphics g, MoveList<Coordinate> possibleMoves) {
        if (mouseTracker.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, UltimateTicTacToePosition.BOARD_WIDTH);
            if (coordinate != null) {
                Coordinate boardNM = getBoardNM(coordinate.x, coordinate.y);
                if (possibleMoves.contains(boardNM)) {
                    GuiPlayerHelper.highlightCoordinate(g, mouseTracker, sizer, 0.1);
                }
            }
        }
    }

    @Override
    public Coordinate maybeGetUserMove(UserInput input, UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(mouseTracker, sizer, UltimateTicTacToePosition.BOARD_WIDTH);
            if (coordinate != null) {
                return getBoardNM(coordinate.x, coordinate.y);
            }
        }
        return null;
    }
}
