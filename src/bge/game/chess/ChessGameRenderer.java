package bge.game.chess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bge.game.Coordinate;
import bge.game.MoveList;
import bge.game.chess.move.IChessMove;
import bge.gui.GameGuiManager;
import bge.gui.gamestate.GuiPlayerHelper;
import bge.gui.gamestate.IGameRenderer;
import bge.gui.gamestate.IPositionObserver;
import bge.main.BoardGameEngineMain;
import gt.gameentity.GridSizer;
import gt.gamestate.UserInput;

public class ChessGameRenderer implements IGameRenderer<IChessMove, ChessPosition>, IPositionObserver<IChessMove, ChessPosition>, ChessConstants {
    private final ChessPieceImages pieceImages;

    private GridSizer sizer;

    private Map<Coordinate, Map<Coordinate, List<IChessMove>>> moveMap = new HashMap<>();
    private Coordinate movingPieceStart;

    public ChessGameRenderer() {
        pieceImages = ChessPieceImages.getInstance();
    }

    @Override
    public void initializeAndDrawBoard(Graphics2D g) {
        int imageWidth = GameGuiManager.getComponentWidth();
        int imageHeight = GameGuiManager.getComponentHeight();

        sizer = new GridSizer(imageWidth, imageHeight, BOARD_WIDTH, BOARD_WIDTH);

        fillRect(g, 0, 0, imageWidth, imageHeight, BoardGameEngineMain.BACKGROUND_COLOR);

        boolean white = true;
        for (int x = 0; x < BOARD_WIDTH; ++x) {
            for (int y = 0; y < BOARD_WIDTH; ++y) {
                Color color = white ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR;
                fillRect(g, sizer.offsetX + sizer.cellSize * x, sizer.offsetY + sizer.cellSize * y, sizer.cellSize + 1, sizer.cellSize + 1, color);
                white = !white;
            }
            white = !white;
        }
    }

    @Override
    public void notifyPositionChanged(ChessPosition position, MoveList<IChessMove> possibleMoves) {
        movingPieceStart = null;
        moveMap = createMoveMap(possibleMoves);
    }

    @Override
    public void drawPosition(Graphics2D g, ChessPosition position, MoveList<IChessMove> possibleMoves, IChessMove lastMove) {
        drawBoard(g, position);
        drawLastMove(g, position, lastMove);
        drawMouseOn(g, position);
    }

    private void drawBoard(Graphics2D g, ChessPosition position) {
        for (int y = 0; y < BOARD_WIDTH; ++y) {
            for (int x = 0; x < BOARD_WIDTH; ++x) {
                if (movingPieceStart != null && movingPieceStart.x == x && movingPieceStart.y == y) {
                    continue;
                }
                int piece = position.squares[SQUARE_64_TO_SQUARE[y][x]];
                BufferedImage pieceImage = pieceImages.getPieceImage(piece);
                if (pieceImage != null) {
                    int x0 = round(sizer.offsetX + sizer.cellSize * x);
                    int y0 = round(sizer.offsetY + sizer.cellSize * y);
                    int width = round(sizer.cellSize + 1);
                    g.drawImage(pieceImage, x0, y0, width, width, null);
                }
            }
        }
    }

    private void drawLastMove(Graphics2D g, ChessPosition positon, IChessMove lastMove) {
        if (lastMove == null) {
            return;
        }
        g.setColor(LAST_MOVE_COLOR);
        Coordinate from = SQUARE_TO_COORDINATE[lastMove.getFrom()];
        GuiPlayerHelper.highlightCoordinate(g, sizer, from.x, from.y, 1.0 / 2.125);
        Coordinate to = SQUARE_TO_COORDINATE[lastMove.getTo()];
        g.setColor(positon.white ? LIGHT_PIECE_COLOR : DARK_PIECE_COLOR);
        GuiPlayerHelper.highlightCoordinate(g, sizer, to.x, to.y, 1.0 / 2.125);
    }

    private void drawMouseOn(Graphics g, ChessPosition position) {
        if (GameGuiManager.isMouseEntered()) { // highlight the cell if the mouse if over a playable move
            Coordinate coordinate = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
            if (movingPieceStart != null) {
                Set<Coordinate> possibleTos = moveMap.get(movingPieceStart).keySet(); // If we are drawing while maybe getting the user move, start can become null
                BufferedImage pieceImage = pieceImages.getPieceImage(position.squares[SQUARE_64_TO_SQUARE[movingPieceStart.y][movingPieceStart.x]]);
                double width = sizer.cellSize + 1;
                int x0 = round(GameGuiManager.getMouseX() - width / 2);
                int y0 = round(GameGuiManager.getMouseY() - width / 2);
                if (possibleTos != null && possibleTos.contains(coordinate)) {
                    GuiPlayerHelper.highlightCoordinate(g, sizer, 1.0 / 32);
                }
                g.drawImage(pieceImage, x0, y0, round(width), round(width), null);
            } else if (coordinate != null && moveMap.containsKey(coordinate)) {
                GuiPlayerHelper.highlightCoordinate(g, sizer, 1.0 / 32);
            }
        }
    }

    private static Map<Coordinate, Map<Coordinate, List<IChessMove>>> createMoveMap(MoveList<IChessMove> possibleMoves) {
        int i = 0;
        Map<Coordinate, Map<Coordinate, List<IChessMove>>> moveMap = new HashMap<>();
        while (i < possibleMoves.size()) {
            IChessMove move = possibleMoves.get(i);
            Coordinate from = SQUARE_TO_COORDINATE[move.getFrom()];
            Map<Coordinate, List<IChessMove>> fromMap = moveMap.get(from);
            if (fromMap == null) {
                fromMap = new HashMap<>();
                moveMap.put(from, fromMap);
            }
            Coordinate to = SQUARE_TO_COORDINATE[move.getTo()];
            List<IChessMove> moves = fromMap.get(to);
            if (moves == null) {
                moves = new ArrayList<>();
                fromMap.put(to, moves);
            }
            moves.add(move);
            ++i;
        }
        return moveMap;
    }

    @Override
    public IChessMove maybeGetUserMove(UserInput input, ChessPosition position, MoveList<IChessMove> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            if (movingPieceStart != null) {
                Coordinate to = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
                if (to != null) {
                    List<IChessMove> moveList = moveMap.get(movingPieceStart).get(to);
                    if (moveList != null) {
                        return moveList.get(0);
                    } // XXX dialog for queening
                }
                movingPieceStart = null;
            }
        } else if (input == UserInput.LEFT_BUTTON_PRESSED) {
            Coordinate from = GuiPlayerHelper.maybeGetCoordinate(sizer, BOARD_WIDTH);
            if (from != null && position.squares[SQUARE_64_TO_SQUARE[from.y][from.x]] != UNPLAYED) {
                if (moveMap.containsKey(from)) { // XXX consider allowing picking up pieces that can't be moved ?
                    movingPieceStart = from;
                }
            }
        }
        return null;
    }
}
