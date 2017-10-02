package game.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class TicTacToePosition implements IPosition<Coordinate, TicTacToePosition> {
	public static final int BOARD_WIDTH = 3;

	int board;
	int currentPlayer;

	public TicTacToePosition() {
		this(0, TwoPlayers.PLAYER_1);
	}

	private TicTacToePosition(int board, int currentPlayer) {
		this.board = board;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<Coordinate> getPossibleMoves() {
		if (TicTacToeUtilities.winExists(board, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<Coordinate> moves = new ArrayList<>();
		if ((board & TicTacToeUtilities.POS_0) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 0));
		}
		if ((board & TicTacToeUtilities.POS_1) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 0));
		}
		if ((board & TicTacToeUtilities.POS_2) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 0));
		}
		if ((board & TicTacToeUtilities.POS_3) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 1));
		}
		if ((board & TicTacToeUtilities.POS_4) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 1));
		}
		if ((board & TicTacToeUtilities.POS_5) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 1));
		}
		if ((board & TicTacToeUtilities.POS_6) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 2));
		}
		if ((board & TicTacToeUtilities.POS_7) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 2));
		}
		if ((board & TicTacToeUtilities.POS_8) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 2));
		}
		return moves;
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(Coordinate move) {
		int shift = (move.y * BOARD_WIDTH + move.x) << 1;
		board |= (currentPlayer << shift);
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(Coordinate move) {
		int shift = (move.y * BOARD_WIDTH + move.x) << 1;
		int boardClear = ~(TwoPlayers.BOTH_PLAYERS << shift);
		board &= boardClear;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public TicTacToePosition createCopy() {
		return new TicTacToePosition(board, currentPlayer);
	}

	@Override
	public String toString() {
		return TicTacToeUtilities.boardToString(board);
	}
}
