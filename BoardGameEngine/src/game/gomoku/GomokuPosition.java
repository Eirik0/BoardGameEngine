package game.gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class GomokuPosition implements IPosition<Coordinate, GomokuPosition> {
	private static final int BOARD_WIDTH = 19;

	private int currentPlayer;
	private final int[][] board;

	public GomokuPosition() {
		this(new int[BOARD_WIDTH][BOARD_WIDTH], TwoPlayers.PLAYER_1);
	}

	public GomokuPosition(int[][] board, int currentPlayer) {
		this.currentPlayer = currentPlayer;
		this.board = board;
	}

	@Override
	public List<Coordinate> getPossibleMoves() {
		if (winsExist(board, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<Coordinate> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			int[] column = board[y];
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (column[x] == TwoPlayers.UNPLAYED) {
					possibleMoves.add(Coordinate.valueOf(y, x));
				}
			}
		}
		return possibleMoves;
	}

	private static boolean winsExist(int[][] board, int player) {
		for (int y = 0; y < BOARD_WIDTH - 4; ++y) {
			for (int x = 0; x < BOARD_WIDTH - 4; ++x) {
				if (board[y][x] == player) {
					if (board[y][x + 1] == player && board[y][x + 2] == player && board[y][x + 3] == player && board[y][x + 4] == player) {
						return true;
					}
					if (board[y + 1][x] == player && board[y + 2][x] == player && board[y + 3][x] == player && board[y + 4][x] == player) {
						return true;
					}
					if (board[y + 1][x + 1] == player && board[y + 2][x + 2] == player && board[y + 3][x + 3] == player && board[y + 4][x + 4] == player) {
						return true;
					}
					if (x >= 4 && board[y + 1][x - 1] == player && board[y + 2][x - 2] == player && board[y + 3][x - 3] == player && board[y + 4][x - 4] == player) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(Coordinate move) {
		board[move.y][move.x] = currentPlayer;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(Coordinate move) {
		board[move.y][move.x] = TwoPlayers.UNPLAYED;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public GomokuPosition createCopy() {
		int[][] boardCopy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for (int y = 0; y < board.length; y++) {
			System.arraycopy(board[y], 0, boardCopy[y], 0, BOARD_WIDTH);
		}
		return new GomokuPosition(boardCopy, currentPlayer);
	}
}
