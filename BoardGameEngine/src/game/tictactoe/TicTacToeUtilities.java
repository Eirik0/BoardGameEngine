package game.tictactoe;

import game.TwoPlayers;

public class TicTacToeUtilities {
	// (0, 0), (1, 0), (2, 0)  0 1 2  ...00000011 ...00001100 ..00110000
	// (0, 1), (1, 1), (2, 1)  3 4 5  ...11000000 ...
	// (0, 2), (1, 2), (2, 2)  6 7 8  ...
	public static final int POS_0 = TwoPlayers.BOTH_PLAYERS << 0;
	public static final int POS_1 = TwoPlayers.BOTH_PLAYERS << 2;
	public static final int POS_2 = TwoPlayers.BOTH_PLAYERS << 4;
	public static final int POS_3 = TwoPlayers.BOTH_PLAYERS << 6;
	public static final int POS_4 = TwoPlayers.BOTH_PLAYERS << 8;
	public static final int POS_5 = TwoPlayers.BOTH_PLAYERS << 10;
	public static final int POS_6 = TwoPlayers.BOTH_PLAYERS << 12;
	public static final int POS_7 = TwoPlayers.BOTH_PLAYERS << 14;
	public static final int POS_8 = TwoPlayers.BOTH_PLAYERS << 16;

	public static boolean winExists(int board, int player) {
		boolean has0 = ((board >> 0) & player) == player;
		boolean has1 = ((board >> 2) & player) == player;
		boolean has2 = ((board >> 4) & player) == player;
		boolean has3 = ((board >> 6) & player) == player;
		boolean has4 = ((board >> 8) & player) == player;
		boolean has5 = ((board >> 10) & player) == player;
		boolean has6 = ((board >> 12) & player) == player;
		boolean has7 = ((board >> 14) & player) == player;
		boolean has8 = ((board >> 16) & player) == player;
		return (has0 && has1 && has2) ||
				(has3 && has4 && has5) ||
				(has6 && has7 && has8) ||
				(has0 && has3 && has6) ||
				(has1 && has4 && has7) ||
				(has2 && has5 && has8) ||
				(has0 && has4 && has8) ||
				(has2 && has4 && has6);
	}

	public static String boardToString(int board) {
		int pos0 = (board >> 0) & TwoPlayers.BOTH_PLAYERS;
		int pos1 = (board >> 2) & TwoPlayers.BOTH_PLAYERS;
		int pos2 = (board >> 4) & TwoPlayers.BOTH_PLAYERS;
		int pos3 = (board >> 6) & TwoPlayers.BOTH_PLAYERS;
		int pos4 = (board >> 8) & TwoPlayers.BOTH_PLAYERS;
		int pos5 = (board >> 10) & TwoPlayers.BOTH_PLAYERS;
		int pos6 = (board >> 12) & TwoPlayers.BOTH_PLAYERS;
		int pos7 = (board >> 14) & TwoPlayers.BOTH_PLAYERS;
		int pos8 = (board >> 16) & TwoPlayers.BOTH_PLAYERS;
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
