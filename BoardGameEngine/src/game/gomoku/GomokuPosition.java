package game.gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class GomokuPosition implements IPosition<Coordinate, GomokuPosition> {
	static final int BOARD_WIDTH = 19;

	int currentPlayer;
	final int[][] board;

	public GomokuPosition() {
		this(new int[BOARD_WIDTH][BOARD_WIDTH], TwoPlayers.PLAYER_1);
	}

	public GomokuPosition(int[][] board, int currentPlayer) {
		this.currentPlayer = currentPlayer;
		this.board = board;
	}

	@Override
	public List<Coordinate> getPossibleMoves() {
		int otherPlayer = TwoPlayers.otherPlayer(currentPlayer);
		if (GomokuPositionEvaluator.winExist(board, otherPlayer)) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<Coordinate> possibleMoves = new ArrayList<>();
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			int[] column = board[y];
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (column[x] == TwoPlayers.UNPLAYED) {
					possibleMoves.add(Coordinate.valueOf(x, y));
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
