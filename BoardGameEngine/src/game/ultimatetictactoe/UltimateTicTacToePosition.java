package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import game.IPosition;
import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;

public class UltimateTicTacToePosition implements IPosition<UTTTCoordinate, UltimateTicTacToePosition> {
	static final int ANY_BOARD = -1;

	static final int BOARD_WIDTH = 9;

	final int[] boards;
	int wonBoards;
	int currentBoard;
	int currentPlayer;

	public UltimateTicTacToePosition() {
		this(new int[BOARD_WIDTH], 0, ANY_BOARD, TwoPlayers.PLAYER_1);
	}

	public UltimateTicTacToePosition(int[] boards, int wonBoards, int currentBoard, int currentPlayer) {
		this.wonBoards = wonBoards;
		this.boards = boards;
		this.currentBoard = currentBoard;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<UTTTCoordinate> getPossibleMoves() {
		if (TicTacToeUtilities.winExists(wonBoards, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<UTTTCoordinate> possibleMoves = new ArrayList<>();
		if (currentBoard == ANY_BOARD) {
			int n = 0;
			while (n < BOARD_WIDTH) {
				if ((wonBoards & TicTacToeUtilities.POS[n]) == TwoPlayers.UNPLAYED) {
					addMovesFromBoard(possibleMoves, n);
				}
				++n;
			}
		} else {
			addMovesFromBoard(possibleMoves, currentBoard);
		}
		return possibleMoves;
	}

	private void addMovesFromBoard(List<UTTTCoordinate> possibleMoves, int boardNum) {
		int board = boards[boardNum];
		int m = 0;
		while (m < BOARD_WIDTH) {
			if ((board & TicTacToeUtilities.POS[m]) == TwoPlayers.UNPLAYED) {
				possibleMoves.add(new UTTTCoordinate(boardNum, m, currentBoard));
			}
			++m;
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(UTTTCoordinate move) {
		int boardNum = move.boardNum;
		int position = move.position;
		boards[boardNum] |= TicTacToeUtilities.PLAYER_POS[currentPlayer][position];
		if (TicTacToeUtilities.winExists(boards[boardNum], currentPlayer)) {
			wonBoards |= TicTacToeUtilities.PLAYER_POS[currentPlayer][boardNum];
		}

		// Check if the new board is won
		if ((wonBoards & TicTacToeUtilities.POS[position]) == TwoPlayers.UNPLAYED) { // not won
			int boardToCheck = boards[position];
			currentBoard = (((boardToCheck << 1) | boardToCheck) & TicTacToeUtilities.PLAYER_2_ALL_POS) == TicTacToeUtilities.PLAYER_2_ALL_POS ? ANY_BOARD : position;
		} else {
			currentBoard = ANY_BOARD;
		}

		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(UTTTCoordinate move) {
		int boardNum = move.boardNum;
		int position = move.position;
		int winShift = boardNum << 1;
		wonBoards &= ~(TwoPlayers.BOTH_PLAYERS << winShift);
		boards[boardNum] &= ~(TwoPlayers.BOTH_PLAYERS << (position << 1));
		currentBoard = move.currentBoard;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public UltimateTicTacToePosition createCopy() {
		int[] newCells = new int[BOARD_WIDTH];
		System.arraycopy(boards, 0, newCells, 0, BOARD_WIDTH);
		return new UltimateTicTacToePosition(newCells, wonBoards, currentBoard, currentPlayer);
	}

	@Override
	public String toString() {
		String boardsString = Arrays.stream(boards).mapToObj(TicTacToeUtilities::boardToString).collect(Collectors.joining(",", "[", "]"));
		return boardsString + "\n" + TicTacToeUtilities.boardToString(wonBoards) + "\n" + currentPlayer + "\n" + currentBoard;
	}
}
