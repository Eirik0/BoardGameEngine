package bge.game.tictactoe;

import java.awt.Color;

import bge.gui.gamestate.IGameRenderer;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.GuiPlayer;
import bge.igame.player.TwoPlayers;
import bge.main.BoardGameEngineMain;
import gt.component.IMouseTracker;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;

public class TicTacToeGameRenderer implements IGameRenderer<Coordinate, TicTacToePosition> {
    private final IMouseTracker mouseTracker;

    private double width;
    private double height;

    public TicTacToeGameRenderer(IMouseTracker mouseTracker) {
        this.mouseTracker = mouseTracker;
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        width = imageWidth;
        height = imageHeight;

        g.fillRect(0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);

        g.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
        for (int i = 1; i < 3; ++i) {
            double thirdOfWidth = i * width / 3.0;
            double thirdOfHeight = i * height / 3.0;
            g.drawLine(thirdOfWidth, 0, thirdOfWidth, height);
            g.drawLine(0, thirdOfHeight, width, thirdOfHeight);
        }
    }

    @Override
    public void drawPosition(IGraphics g, TicTacToePosition position, MoveList<Coordinate> possibleMoves, Coordinate lastMove) {
        for (int y = 0; y < TicTacToePosition.BOARD_WIDTH; ++y) {
            for (int x = 0; x < TicTacToePosition.BOARD_WIDTH; ++x) {
                int playerInt = getPlayer(position.board, x, y);
                if (playerInt != TwoPlayers.UNPLAYED) {
                    g.setColor(lastMove != null && Coordinate.valueOf(x, y).equals(lastMove) ? Color.RED : BoardGameEngineMain.FOREGROUND_COLOR);
                    String player = playerInt == TwoPlayers.PLAYER_1 ? "X" : "O";
                    double xCoord = width * (2 * x + 1) / 6.0;
                    double yCoord = height * (2 * y + 1) / 6.0;
                    g.drawCenteredString(player, xCoord, yCoord);
                }
            }
        }
    }

    private static int getPlayer(int board, int x, int y) {
        int shift = (y * TicTacToePosition.BOARD_WIDTH + x) * 2;
        return (board >> shift) & TwoPlayers.BOTH_PLAYERS;
    }

    @Override
    public Coordinate maybeGetUserMove(UserInput input, TicTacToePosition position, MoveList<Coordinate> possibleMoves) {
        if (input == UserInput.LEFT_BUTTON_RELEASED) {
            if (GuiPlayer.HUMAN.isRequestingMove()) {
                double x = 3 * mouseTracker.mouseX() / width;
                double y = 3 * mouseTracker.mouseY() / height;
                return Coordinate.valueOf((int) x, (int) y);
            }
        }
        return null;
    }
}
