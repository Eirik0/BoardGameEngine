package game.ultimatetictactoe;

import java.util.Arrays;
import java.util.stream.Collectors;

import game.Coordinate;
import game.IPosition;
import game.MoveList;
import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;

public class UltimateTicTacToePosition implements IPosition<Coordinate, UltimateTicTacToePosition> {
	static final int BOARD_WIDTH = 9;

	static final int ANY_BOARD = -1;

	static final int MAX_MOVES = 81;

	final int[] boards;
	int wonBoards;
	int fullBoards;
	int currentBoard;
	int currentPlayer;

	final int[] currentBoardHistory;
	int plyCount;

	public UltimateTicTacToePosition() {
		this(new int[BOARD_WIDTH], 0, 0, ANY_BOARD, TwoPlayers.PLAYER_1, new int[MAX_MOVES], 0);
	}

	public UltimateTicTacToePosition(int[] boards, int wonBoards, int fullBoards, int currentBoard, int currentPlayer, int[] currentBoardHistory, int plyCount) {
		this.wonBoards = wonBoards;
		this.boards = boards;
		this.currentBoard = currentBoard;
		this.currentPlayer = currentPlayer;
		this.currentBoardHistory = currentBoardHistory;
		this.plyCount = plyCount;
	}

	@Override
	public void getPossibleMoves(MoveList<Coordinate> possibleMoves) {
		if (TicTacToeUtilities.winExists(wonBoards, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return;
		}
		if (currentBoard == ANY_BOARD) {
			int n = 0;
			while (n < BOARD_WIDTH) {
				if (((wonBoards | fullBoards) & TicTacToeUtilities.POS[n]) == TwoPlayers.UNPLAYED) {
					addMovesFromBoard(possibleMoves, n);
				}
				++n;
			}
		} else {
			addMovesFromBoard(possibleMoves, currentBoard);
		}
	}

	private void addMovesFromBoard(MoveList<Coordinate> possibleMoves, int boardNum) {
		int board = boards[boardNum];
		int m = 0;
		while (m < BOARD_WIDTH) {
			if ((board & TicTacToeUtilities.POS[m]) == TwoPlayers.UNPLAYED) {
				possibleMoves.add(Coordinate.valueOf(boardNum, m));
			}
			++m;
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(Coordinate move) {
		currentBoardHistory[plyCount++] = currentBoard;

		int boardNum = move.x;
		int position = move.y;
		boards[boardNum] |= TicTacToeUtilities.PLAYER_POS[currentPlayer][position];
		if (TicTacToeUtilities.winExists(boards[boardNum], currentPlayer)) {
			wonBoards |= TicTacToeUtilities.PLAYER_POS[currentPlayer][boardNum];
		}

		// Check if the new board is won
		if ((wonBoards & TicTacToeUtilities.POS[position]) == TwoPlayers.UNPLAYED) { // not won
			int boardToCheck = boards[position];
			boolean isFull = (((boardToCheck << 1) | boardToCheck) & TicTacToeUtilities.PLAYER_2_ALL_POS) == TicTacToeUtilities.PLAYER_2_ALL_POS;
			if (isFull) {
				fullBoards |= TicTacToeUtilities.POS[boardNum];
				currentBoard = ANY_BOARD;
			} else {
				currentBoard = position;
			}
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
		fullBoards &= undoWonBoard;
		boards[boardNum] &= ~(TwoPlayers.BOTH_PLAYERS << (move.y << 1));

		currentBoard = currentBoardHistory[--plyCount];
	}

	@Override
	public UltimateTicTacToePosition createCopy() {
		int[] cellsCopy = new int[BOARD_WIDTH];
		System.arraycopy(boards, 0, cellsCopy, 0, BOARD_WIDTH);
		int[] currentBoardHistoryCopy = new int[MAX_MOVES];
		System.arraycopy(currentBoardHistory, 0, currentBoardHistoryCopy, 0, MAX_MOVES);
		return new UltimateTicTacToePosition(cellsCopy, wonBoards, fullBoards, currentBoard, currentPlayer, currentBoardHistoryCopy, plyCount);
	}

	@Override
	public String toString() {
		String boardsString = Arrays.stream(boards).mapToObj(TicTacToeUtilities::boardToString).collect(Collectors.joining(",", "[", "]"));
		return boardsString + "\n" + TicTacToeUtilities.boardToString(wonBoards) + "\n" + currentPlayer + "\n" + currentBoard;
	}
}
