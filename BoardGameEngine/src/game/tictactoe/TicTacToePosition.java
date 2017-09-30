package game.tictactoe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.Coordinate;
import game.IPosition;
import game.TwoPlayers;

public class TicTacToePosition implements IPosition<Coordinate, TicTacToePosition> {
	public static final int BOARD_WIDTH = 3;
	// (0, 0), (1, 0), (2, 0)  0 1 2  ...00000011 ...00001100 ..00110000
	// (0, 1), (1, 1), (2, 1)  3 4 5  ...11000000 ...
	// (0, 2), (1, 2), (2, 2)  6 7 8  ...
	private static final int POS_0 = TwoPlayers.BOTH_PLAYERS << 0;
	private static final int POS_1 = TwoPlayers.BOTH_PLAYERS << 2;
	private static final int POS_2 = TwoPlayers.BOTH_PLAYERS << 4;
	private static final int POS_3 = TwoPlayers.BOTH_PLAYERS << 6;
	private static final int POS_4 = TwoPlayers.BOTH_PLAYERS << 8;
	private static final int POS_5 = TwoPlayers.BOTH_PLAYERS << 10;
	private static final int POS_6 = TwoPlayers.BOTH_PLAYERS << 12;
	private static final int POS_7 = TwoPlayers.BOTH_PLAYERS << 14;
	private static final int POS_8 = TwoPlayers.BOTH_PLAYERS << 16;

	int currentPlayer;

	int board;

	public TicTacToePosition() {
		this(0, TwoPlayers.PLAYER_1);
	}

	private TicTacToePosition(int board, int currentPlayer) {
		this.board = board;
		this.currentPlayer = currentPlayer;
	}

	@Override
	public List<Coordinate> getPossibleMoves() {
		if (winsExist(board, TwoPlayers.otherPlayer(currentPlayer))) { // We only need to check the last player who played
			return Collections.emptyList();
		}
		List<Coordinate> moves = new ArrayList<>();
		if ((board & POS_0) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 0));
		}
		if ((board & POS_1) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 0));
		}
		if ((board & POS_2) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 0));
		}
		if ((board & POS_3) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 1));
		}
		if ((board & POS_4) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 1));
		}
		if ((board & POS_5) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 1));
		}
		if ((board & POS_6) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(0, 2));
		}
		if ((board & POS_7) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(1, 2));
		}
		if ((board & POS_8) == TwoPlayers.UNPLAYED) {
			moves.add(Coordinate.valueOf(2, 2));
		}
		return moves;
	}

	public static boolean winsExist(int board, int player) {
		boolean has0 = (((board & POS_0) >> 0) & player) == player;
		boolean has1 = (((board & POS_1) >> 2) & player) == player;
		boolean has2 = (((board & POS_2) >> 4) & player) == player;
		boolean has3 = (((board & POS_3) >> 6) & player) == player;
		boolean has4 = (((board & POS_4) >> 8) & player) == player;
		boolean has5 = (((board & POS_5) >> 10) & player) == player;
		boolean has6 = (((board & POS_6) >> 12) & player) == player;
		boolean has7 = (((board & POS_7) >> 14) & player) == player;
		boolean has8 = (((board & POS_8) >> 16) & player) == player;
		return (has0 && has1 && has2) ||
				(has3 && has4 && has5) ||
				(has6 && has7 && has8) ||
				(has0 && has3 && has6) ||
				(has1 && has4 && has7) ||
				(has2 && has5 && has8) ||
				(has0 && has4 && has8) ||
				(has2 && has4 && has6);
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(Coordinate move) {
		int shift = (move.y * BOARD_WIDTH + move.x) << 1;
		board = board | (currentPlayer << shift);
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(Coordinate move) {
		int shift = (move.y * BOARD_WIDTH + move.x) << 1;
		int boardClear = ~(TwoPlayers.BOTH_PLAYERS << shift);
		board = board & boardClear;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public TicTacToePosition createCopy() {
		return new TicTacToePosition(board, currentPlayer);
	}

	@Override
	public String toString() {
		int pos0 = ((board & POS_0) >> 0) & TwoPlayers.BOTH_PLAYERS;
		int pos1 = ((board & POS_1) >> 2) & TwoPlayers.BOTH_PLAYERS;
		int pos2 = ((board & POS_2) >> 4) & TwoPlayers.BOTH_PLAYERS;
		int pos3 = ((board & POS_3) >> 6) & TwoPlayers.BOTH_PLAYERS;
		int pos4 = ((board & POS_4) >> 8) & TwoPlayers.BOTH_PLAYERS;
		int pos5 = ((board & POS_0) >> 10) & TwoPlayers.BOTH_PLAYERS;
		int pos6 = ((board & POS_6) >> 12) & TwoPlayers.BOTH_PLAYERS;
		int pos7 = ((board & POS_7) >> 14) & TwoPlayers.BOTH_PLAYERS;
		int pos8 = ((board & POS_8) >> 16) & TwoPlayers.BOTH_PLAYERS;
		String pos0tr = (pos0 == TwoPlayers.UNPLAYED) ? " " : (pos0 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos1tr = (pos1 == TwoPlayers.UNPLAYED) ? " " : (pos1 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos2tr = (pos2 == TwoPlayers.UNPLAYED) ? " " : (pos2 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos3tr = (pos3 == TwoPlayers.UNPLAYED) ? " " : (pos3 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos4tr = (pos4 == TwoPlayers.UNPLAYED) ? " " : (pos4 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos5tr = (pos5 == TwoPlayers.UNPLAYED) ? " " : (pos5 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos6tr = (pos6 == TwoPlayers.UNPLAYED) ? " " : (pos6 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos7tr = (pos7 == TwoPlayers.UNPLAYED) ? " " : (pos7 == TwoPlayers.PLAYER_1) ? "X" : "O";
		String pos8tr = (pos8 == TwoPlayers.UNPLAYED) ? " " : (pos8 == TwoPlayers.PLAYER_1) ? "X" : "O";
		return "[" + pos0tr + pos1tr + pos2tr + "],[" + pos3tr + pos4tr + pos5tr + "],[" + pos6tr + pos7tr + pos8tr + "]";
	}
}
