package bge.game.ultimatetictactoe;

import java.util.Arrays;
import java.util.stream.Collectors;

import bge.game.tictactoe.TicTacToeUtilities;
import bge.igame.Coordinate;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

public class UltimateTicTacToePosition implements IPosition<Coordinate> {
    static final int BOARD_WIDTH = 9;
    static final int ANY_BOARD = -1;
    static final int MAX_MOVES = 81;

    final int[] boards;
    int wonBoards;
    int currentBoard;
    int currentPlayer;

    final int[] currentBoardHistory;
    int plyCount;

    public UltimateTicTacToePosition() {
        this(new int[BOARD_WIDTH], 0, ANY_BOARD, TwoPlayers.PLAYER_1, new int[MAX_MOVES], 0);
    }

    public UltimateTicTacToePosition(int[] boards, int wonBoards, int currentBoard, int currentPlayer, int[] currentBoardHistory, int plyCount) {
        this.boards = boards;
        this.wonBoards = wonBoards;
        this.currentBoard = currentBoard;
        this.currentPlayer = currentPlayer;
        this.currentBoardHistory = currentBoardHistory;
        this.plyCount = plyCount;
    }

    @Override
    public void getPossibleMoves(MoveList<Coordinate> possibleMoves) {
        if (UltimateTicTacToeUtilities.winExists(wonBoards, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
            return;
        }
        if (currentBoard == ANY_BOARD) {
            int n = 0;
            while (n < BOARD_WIDTH) {
                if ((wonBoards & (TwoPlayers.BOTH_PLAYERS << (n << 1))) == TwoPlayers.UNPLAYED) {
                    addMovesFromBoard(possibleMoves, n);
                }
                ++n;
            }
        } else {
            addMovesFromBoard(possibleMoves, currentBoard);
        }
    }

    private void addMovesFromBoard(MoveList<Coordinate> possibleMoves, int boardNum) {
        int[] dynamicMoves = UltimateTicTacToeUtilities.getDynamicMoves(boards[boardNum], currentPlayer);
        int i = 0;
        while (i < dynamicMoves.length) {
            possibleMoves.addDynamicMove(Coordinate.valueOf(boardNum, dynamicMoves[i]), this);
            ++i;
        }
        int[] quietMoves = UltimateTicTacToeUtilities.getQuietMoves(boards[boardNum], currentPlayer);
        i = 0;
        while (i < quietMoves.length) {
            possibleMoves.addQuietMove(Coordinate.valueOf(boardNum, quietMoves[i]), this);
            ++i;
        }
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void makeMove(Coordinate move) {
        currentBoardHistory[plyCount++] = currentBoard;

        int boardNum = move.x << 1;
        int position = move.y << 1;
        int oldBoard = boards[move.x] |= currentPlayer << position;

        if (UltimateTicTacToeUtilities.winExists(oldBoard, currentPlayer)) {
            wonBoards |= currentPlayer << boardNum;
        } else
        // Check if the old board is full
        if ((((oldBoard << 1) | oldBoard) & TicTacToeUtilities.PLAYER_2_ALL_POS) == TicTacToeUtilities.PLAYER_2_ALL_POS) {
            wonBoards |= TwoPlayers.BOTH_PLAYERS << boardNum;
        }

        // Check if the new board is won or full
        if ((wonBoards & (TwoPlayers.BOTH_PLAYERS << position)) == TwoPlayers.UNPLAYED) {
            currentBoard = move.y;
        } else {
            currentBoard = ANY_BOARD;
        }

        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
    }

    @Override
    public void unmakeMove(Coordinate move) {
        currentPlayer = TwoPlayers.otherPlayer(currentPlayer);

        int boardNum = move.x;
        int undoWonBoard = ~(TwoPlayers.BOTH_PLAYERS << (boardNum << 1));
        wonBoards &= undoWonBoard;
        boards[boardNum] ^= currentPlayer << (move.y << 1);

        currentBoard = currentBoardHistory[--plyCount];
    }

    @Override
    public UltimateTicTacToePosition createCopy() {
        int[] cellsCopy = new int[BOARD_WIDTH];
        System.arraycopy(boards, 0, cellsCopy, 0, BOARD_WIDTH);
        int[] currentBoardHistoryCopy = new int[MAX_MOVES];
        System.arraycopy(currentBoardHistory, 0, currentBoardHistoryCopy, 0, MAX_MOVES);
        return new UltimateTicTacToePosition(cellsCopy, wonBoards, currentBoard, currentPlayer, currentBoardHistoryCopy, plyCount);
    }

    @Override
    public String toString() {
        String boardsString = Arrays.stream(boards).mapToObj(TicTacToeUtilities::boardToString).collect(Collectors.joining(",", "[", "]"));
        return boardsString + "\n" + TicTacToeUtilities.boardToString(wonBoards) + "\n" + currentPlayer + "\n" + currentBoard;
    }
}
