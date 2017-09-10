package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class UltimateTicTacToePosition implements IPosition<UTTTCoordinate, UltimateTicTacToePosition> {
	static final int ANY_BOARD = -1;

	static final int BOARD_WIDTH = 9;

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
		this(new int[BOARD_WIDTH][BOARD_WIDTH], new int[BOARD_WIDTH], ANY_BOARD, TwoPlayers.PLAYER_1);
	}

	public UltimateTicTacToePosition(int[][] cells, int[] wonBoards, int currentBoard, int currentPlayer) {
		this.wonBoards = wonBoards;
		this.cells = cells;
		this.currentBoard = currentBoard;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<UTTTCoordinate> getPossibleMoves() {
		if (UltimateTicTacToeUtilities.winsExist(wonBoards, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<UTTTCoordinate> possibleMoves = new ArrayList<>();
		if (currentBoard == ANY_BOARD) {
			for (int n = 0; n < BOARD_WIDTH; ++n) {
				if (wonBoards[n] == TwoPlayers.UNPLAYED) {
					int[] board = cells[n];
					for (int m = 0; m < BOARD_WIDTH; ++m) {
						if (board[m] == TwoPlayers.UNPLAYED) {
							possibleMoves.add(new UTTTCoordinate(UltimateTicTacToeUtilities.getBoardXY(n, m), currentBoard));
						}
					}
				}
			}
		} else {
			for (int m = 0; m < BOARD_WIDTH; ++m) {
				if (cells[currentBoard][m] == TwoPlayers.UNPLAYED) {
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
		}
		if (wonBoards[boardNM.y] != TwoPlayers.UNPLAYED) {
			currentBoard = ANY_BOARD;
		} else {
			boolean full = true;
			for (int i = 0; i < BOARD_WIDTH; ++i) {
				if (cells[boardNM.y][i] == TwoPlayers.UNPLAYED) {
					full = false;
					break;
				}
			}
			currentBoard = full ? ANY_BOARD : boardNM.y;
		}

		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(UTTTCoordinate move) {
		Coordinate boardNM = UltimateTicTacToeUtilities.getBoardNM(move.coordinate.x, move.coordinate.y);
		int[] boardInPlay = cells[boardNM.x];
		wonBoards[boardNM.x] = TwoPlayers.UNPLAYED;
		boardInPlay[boardNM.y] = TwoPlayers.UNPLAYED;
		currentBoard = move.currentBoard;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
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
		return Arrays.deepToString(cells) + "\n" + Arrays.toString(wonBoards) + "\n" + currentPlayer + "\n" + currentBoard;
	}
}
