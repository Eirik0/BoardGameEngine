package game.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.Coordinate;
import game.IPosition;

public class TicTacToePosition implements IPosition<Coordinate, TicTacToePosition> {
	int currentPlayer;
	final int[][] board;

	public TicTacToePosition() {
		this(new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, 1);
	}

	private TicTacToePosition(int[][] board, int currentPlayer) {
		this.board = board;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<Coordinate> getPossibleMoves() {
		List<Coordinate> moves = new ArrayList<>();
		for (int y = 0; y < board.length; y++) {
			int[] row = board[y];
			for (int x = 0; x < row.length; x++) {
				if (row[x] == 0) {
					moves.add(new Coordinate(x, y));
				}
			}
		}
		return moves;
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(Coordinate move) {
		board[move.y][move.x] = currentPlayer;
		switchPlayer();
	}

	@Override
	public void unmakeMove(Coordinate move) {
		board[move.y][move.x] = 0;
		switchPlayer();
	}

	@Override
	public TicTacToePosition createCopy() {
		int[][] boardCopy = new int[3][3];
		for (int y = 0; y < board.length; y++) {
			System.arraycopy(board[y], 0, boardCopy[y], 0, 2);
		}
		return new TicTacToePosition(boardCopy, currentPlayer);
	}

	private void switchPlayer() {
		currentPlayer = currentPlayer == 1 ? 2 : 1;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(board);
	}
}
