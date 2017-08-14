package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;

public class UltimateTicTacToePosition implements IPosition<UTTTCoordinate, UltimateTicTacToePosition> {
	private static final int ANY_BOARD = -1;

	static final int BOARD_WIDTH = 9;
	static final int UNPLAYED = 0;
	static final int PLAYER_1 = 1;
	static final int PLAYER_2 = 2;
	static final int BOTH_PLAYERS = PLAYER_1 & PLAYER_2;

	/**
	 * 9 copies of:<br>
	 * <br>
	 * 0 1 2<br>
	 * 3 4 5<br>
	 * 6 7 8<br>
	 */
	final int[][] cells;
	final int[] wonBoards;
	int currentBoard;
	int currentPlayer;

	public UltimateTicTacToePosition() {
		this(new int[BOARD_WIDTH][BOARD_WIDTH], new int[BOARD_WIDTH], ANY_BOARD, PLAYER_1);
	}

	public UltimateTicTacToePosition(int[][] cells, int[] wonBoards, int currentBoard, int currentPlayer) {
		this.wonBoards = wonBoards;
		this.cells = cells;
		this.currentBoard = currentBoard;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<UTTTCoordinate> getPossibleMoves() {
		if (UltimateTicTacToeUtilities.winsExist(wonBoards, PLAYER_1) || UltimateTicTacToeUtilities.winsExist(wonBoards, PLAYER_1)) {
			return Collections.emptyList();
		}
		List<UTTTCoordinate> possibleMoves = new ArrayList<>();
		if (currentBoard == ANY_BOARD) {
			for (int n = 0; n < BOARD_WIDTH; ++n) {
				if (wonBoards[n] == UNPLAYED) {
					int[] board = cells[n];
					for (int m = 0; m < BOARD_WIDTH; ++m) {
						if (board[m] == UNPLAYED) {
							possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(n, m), currentBoard));
						}
					}
				}
			}
		} else {
			for (int m = 0; m < BOARD_WIDTH; ++m) {
				if (cells[currentBoard][m] == UNPLAYED) {
					possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(currentBoard, m), currentBoard));
				}
			}
		}
		return possibleMoves;
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(UTTTCoordinate move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		int[] boardInPlay = cells[boardNM.x];
		boardInPlay[boardNM.y] = currentPlayer;
		if (UltimateTicTacToeUtilities.winsExist(boardInPlay, currentPlayer)) {
			wonBoards[boardNM.x] = currentPlayer;
		} else {
			boolean full = true;
			for (int i = 0; i < BOARD_WIDTH; ++i) {
				if (boardInPlay[i] == UNPLAYED) {
					full = false;
					break;
				}
			}
			if (full) {
				wonBoards[boardNM.x] = BOTH_PLAYERS;
			}
		}
		currentBoard = wonBoards[boardNM.y] == UNPLAYED ? boardNM.y : ANY_BOARD;
		currentPlayer = currentPlayer == PLAYER_1 ? PLAYER_2 : PLAYER_1;
	}

	@Override
	public void unmakeMove(UTTTCoordinate move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		int[] boardInPlay = cells[boardNM.x];
		wonBoards[boardNM.x] = UNPLAYED;
		boardInPlay[boardNM.y] = UNPLAYED;
		currentBoard = move.currentBoard;
		currentPlayer = currentPlayer == PLAYER_1 ? PLAYER_2 : PLAYER_1;
	}

	@Override
	public UltimateTicTacToePosition createCopy() {
		int[][] newCells = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int i = 0; i < BOARD_WIDTH; ++i) {
			System.arraycopy(cells[i], 0, newCells[i], 0, BOARD_WIDTH);
		}
		int[] newWonBoards = new int[BOARD_WIDTH];
		System.arraycopy(wonBoards, 0, newWonBoards, 0, BOARD_WIDTH);
		return new UltimateTicTacToePosition(newCells, newWonBoards, currentBoard, currentPlayer);
	}

	@Override
	public String toString() {
		return Arrays.deepToString(cells);
	}
}
