package bge.game.gomoku;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

public class GomokuPosition implements IPosition<Integer> {
    int currentPlayer;
    final int[] board;
    boolean gameOver;

    public GomokuPosition() {
        this(GomokuUtilities.newInitialPosition(), TwoPlayers.PLAYER_1, false);
    }

    private GomokuPosition(int[] board, int currentPlayer, boolean gameOver) {
        this.currentPlayer = currentPlayer;
        this.board = board;
        this.gameOver = gameOver;
    }

    @Override
    public void getPossibleMoves(MoveList<Integer> possibleMoves) {
        if (gameOver) {
            return;
        }
        int i = GomokuUtilities.START_BOARD_INDEX;
        do {
            int j = 0;
            do {
                if (board[i] == TwoPlayers.UNPLAYED) {
                    possibleMoves.addQuietMove(GomokuUtilities.MOVES[i], this);
                }
                ++i;
            } while (++j < GomokuUtilities.BOARD_WIDTH);
        } while (++i < GomokuUtilities.FINAL_BOARD_INDEX);
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void makeMove(Integer move) {
        int moveInt = move.intValue();
        board[moveInt] = currentPlayer;
        gameOver = GomokuUtilities.winExists(board, moveInt, currentPlayer);
        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
    }

    @Override
    public void unmakeMove(Integer move) {
        board[move.intValue()] = TwoPlayers.UNPLAYED;
        gameOver = false;
        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
    }

    @Override
    public GomokuPosition createCopy() {
        int[] boardCopy = new int[GomokuUtilities.BOARD_SIZE];
        System.arraycopy(board, 0, boardCopy, 0, GomokuUtilities.BOARD_SIZE);
        return new GomokuPosition(boardCopy, currentPlayer, gameOver);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = GomokuUtilities.START_BOARD_INDEX;
        do {
            int j = 0;
            do {
                sb.append(board[i] == TwoPlayers.UNPLAYED ? " " : board[i] == TwoPlayers.PLAYER_1 ? "X" : "O");
                ++i;
            } while (++j < GomokuUtilities.BOARD_WIDTH);
            sb.append("\n");
        } while (++i < GomokuUtilities.FINAL_BOARD_INDEX);
        return sb.toString();
    }
}
