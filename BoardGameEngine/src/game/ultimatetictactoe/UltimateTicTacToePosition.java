package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import game.Coordinate;
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
			for (int n = 0; n < BOARD_WIDTH; ++n) {
				if (((wonBoards >> (n << 1)) & TwoPlayers.BOTH_PLAYERS) == TwoPlayers.UNPLAYED) {
					addMovesFromBoard(possibleMoves, n);
				}
			}
		} else {
			addMovesFromBoard(possibleMoves, currentBoard);
		}
		return possibleMoves;
	}

	private void addMovesFromBoard(List<UTTTCoordinate> possibleMoves, int board) {
		if ((boards[board] & TicTacToeUtilities.POS_0) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 0), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_1) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 1), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_2) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 2), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_3) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 3), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_4) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 4), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_5) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 5), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_6) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 6), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_7) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 7), currentBoard));
		}
		if ((boards[board] & TicTacToeUtilities.POS_8) == TwoPlayers.UNPLAYED) {
			possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(board, 8), currentBoard));
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(UTTTCoordinate move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		int shift = boardNM.y << 1;
		boards[boardNM.x] |= (currentPlayer << shift);
		if (TicTacToeUtilities.winExists(boards[boardNM.x], currentPlayer)) {
			int winShift = boardNM.x << 1;
			wonBoards |= (currentPlayer << winShift);
		}

		// Check if the new board is won
		if ((wonBoards & (TwoPlayers.BOTH_PLAYERS << shift)) != TwoPlayers.UNPLAYED) {
			currentBoard = ANY_BOARD;
		} else { // or full
			int boardToCheck = boards[boardNM.y];
			boolean notFull = (boardToCheck & TwoPlayers.BOTH_PLAYERS) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 2) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 4) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 6) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 8) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 10) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 12) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 14) == TwoPlayers.UNPLAYED ||
					(boardToCheck & TwoPlayers.BOTH_PLAYERS << 16) == TwoPlayers.UNPLAYED;
			currentBoard = notFull ? boardNM.y : ANY_BOARD;
		}

		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(UTTTCoordinate move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		int winShift = boardNM.x << 1;
		int winBoardClear = ~(TwoPlayers.BOTH_PLAYERS << winShift);
		wonBoards &= winBoardClear;
		int shift = boardNM.y << 1;
		int boardClear = ~(TwoPlayers.BOTH_PLAYERS << shift);
		boards[boardNM.x] &= boardClear;
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
