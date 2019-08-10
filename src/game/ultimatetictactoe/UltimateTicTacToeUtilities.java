package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.List;

import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;
import util.Pair;

public class UltimateTicTacToeUtilities {
	public static final int MAX_REASONABLE_DEPTH = 63;

	private static final int MAX_BOARD_NUM = 1 << 18;

	private static final int PLAYER_2_MASK = 1 << 19;

	private static final boolean[] hasPossibleWins = new boolean[PLAYER_2_MASK | MAX_BOARD_NUM]; // player, board
	private static final boolean[] winsExist = new boolean[PLAYER_2_MASK | MAX_BOARD_NUM]; // player, board

	private static final int[] countPossibleWins = new int[PLAYER_2_MASK | MAX_BOARD_NUM]; // player, board
	private static final int[][] dynamicMoves = new int[PLAYER_2_MASK | MAX_BOARD_NUM][]; // player, board
	private static final int[][] quietMoves = new int[PLAYER_2_MASK | MAX_BOARD_NUM][]; // player, board

	static {
		for (int board = 0; board < MAX_BOARD_NUM; ++board) {
			// We need has possible wins for "invalid" boards
			hasPossibleWins[board] = calcHasPossibleWins(board, TwoPlayers.PLAYER_1);
			hasPossibleWins[PLAYER_2_MASK | board] = calcHasPossibleWins(board, TwoPlayers.PLAYER_2);
			if (!checkValidBoard(board)) {
				continue;
			}
			winsExist[board] = TicTacToeUtilities.winExists(board, TwoPlayers.PLAYER_1);
			winsExist[PLAYER_2_MASK | board] = TicTacToeUtilities.winExists(board, TwoPlayers.PLAYER_2);
			countPossibleWins[board] = calcCountPossibleWins(board, TwoPlayers.PLAYER_1);
			countPossibleWins[PLAYER_2_MASK | board] = calcCountPossibleWins(board, TwoPlayers.PLAYER_2);
			Pair<List<Integer>, List<Integer>> p1Moves = calculatePossibleMoves(board, TwoPlayers.PLAYER_1);
			Pair<List<Integer>, List<Integer>> p2Moves = calculatePossibleMoves(board, TwoPlayers.PLAYER_2);
			List<Integer> p1DymanicMoves = p1Moves.getFirst();
			List<Integer> p1QuietMoves = p1Moves.getSecond();
			List<Integer> p2DymanicMoves = p2Moves.getFirst();
			List<Integer> p2QuietMoves = p2Moves.getSecond();
			int[] p1DynamicMovesCoords = new int[p1DymanicMoves.size()];
			int[] p1QuietMovesCoords = new int[p1QuietMoves.size()];
			int[] p2DynamicMovesCoords = new int[p2DymanicMoves.size()];
			int[] p2QuietMovesCoords = new int[p2QuietMoves.size()];
			for (int i = 0; i < p1DymanicMoves.size(); ++i) {
				p1DynamicMovesCoords[i] = p1DymanicMoves.get(i).intValue();
			}
			for (int i = 0; i < p1QuietMoves.size(); ++i) {
				p1QuietMovesCoords[i] = p1QuietMoves.get(i).intValue();
			}
			for (int i = 0; i < p2DymanicMoves.size(); ++i) {
				p2DynamicMovesCoords[i] = p2DymanicMoves.get(i).intValue();
			}
			for (int i = 0; i < p2QuietMoves.size(); ++i) {
				p2QuietMovesCoords[i] = p2QuietMoves.get(i).intValue();
			}
			dynamicMoves[board] = p1DynamicMovesCoords;
			quietMoves[board] = p1QuietMovesCoords;
			dynamicMoves[PLAYER_2_MASK | board] = p2DynamicMovesCoords;
			quietMoves[PLAYER_2_MASK | board] = p2QuietMovesCoords;
		}
	}

	private static int calcCountPossibleWins(int board, int otherPlayer) {
		boolean has0 = ((board >> 0) & otherPlayer) != otherPlayer;
		boolean has1 = ((board >> 2) & otherPlayer) != otherPlayer;
		boolean has2 = ((board >> 4) & otherPlayer) != otherPlayer;
		boolean has3 = ((board >> 6) & otherPlayer) != otherPlayer;
		boolean has4 = ((board >> 8) & otherPlayer) != otherPlayer;
		boolean has5 = ((board >> 10) & otherPlayer) != otherPlayer;
		boolean has6 = ((board >> 12) & otherPlayer) != otherPlayer;
		boolean has7 = ((board >> 14) & otherPlayer) != otherPlayer;
		boolean has8 = ((board >> 16) & otherPlayer) != otherPlayer;
		return (has0 && has1 && has2 ? 1 : 0) +
				(has3 && has4 && has5 ? 1 : 0) +
				(has6 && has7 && has8 ? 1 : 0) +
				(has0 && has3 && has6 ? 1 : 0) +
				(has1 && has4 && has7 ? 1 : 0) +
				(has2 && has5 && has8 ? 1 : 0) +
				(has0 && has4 && has8 ? 1 : 0) +
				(has2 && has4 && has6 ? 1 : 0);
	}

	private static boolean calcHasPossibleWins(int board, int otherPlayer) {
		boolean has0 = ((board >> 0) & otherPlayer) != otherPlayer;
		boolean has1 = ((board >> 2) & otherPlayer) != otherPlayer;
		boolean has2 = ((board >> 4) & otherPlayer) != otherPlayer;
		boolean has3 = ((board >> 6) & otherPlayer) != otherPlayer;
		boolean has4 = ((board >> 8) & otherPlayer) != otherPlayer;
		boolean has5 = ((board >> 10) & otherPlayer) != otherPlayer;
		boolean has6 = ((board >> 12) & otherPlayer) != otherPlayer;
		boolean has7 = ((board >> 14) & otherPlayer) != otherPlayer;
		boolean has8 = ((board >> 16) & otherPlayer) != otherPlayer;
		return (has0 && has1 && has2) ||
				(has3 && has4 && has5) ||
				(has6 && has7 && has8) ||
				(has0 && has3 && has6) ||
				(has1 && has4 && has7) ||
				(has2 && has5 && has8) ||
				(has0 && has4 && has8) ||
				(has2 && has4 && has6);
	}

	private UltimateTicTacToeUtilities() {
	}

	public static void initialize() {
		// Do nothing
	}

	private static boolean checkValidBoard(int board) {
		int i = 0;
		do {
			if ((board & TicTacToeUtilities.POS[i]) == TicTacToeUtilities.POS[i]) {
				return false;
			}
		} while (++i < UltimateTicTacToePosition.BOARD_WIDTH);
		return true;
	}

	private static Pair<List<Integer>, List<Integer>> calculatePossibleMoves(int board, int currentPlayer) {
		List<Integer> dynamicMoves = new ArrayList<>();
		List<Integer> quietMoves = new ArrayList<>();
		int m = 0;
		do {
			if ((board & TicTacToeUtilities.POS[m]) == TwoPlayers.UNPLAYED) {
				if (TicTacToeUtilities.winExists(board | TicTacToeUtilities.getPlayerAtPosition(currentPlayer, m), currentPlayer)) {
					dynamicMoves.add(Integer.valueOf(m));
				} else {
					quietMoves.add(Integer.valueOf(m));
				}
			}
		} while (++m < UltimateTicTacToePosition.BOARD_WIDTH);
		return Pair.valueOf(dynamicMoves, quietMoves);
	}

	public static boolean winExists(int board, int player) {
		return winsExist[player == TwoPlayers.PLAYER_1 ? board : PLAYER_2_MASK | board];
	}

	public static int countPossibleWins(int board, int otherPlayer) {
		return countPossibleWins[otherPlayer == TwoPlayers.PLAYER_1 ? board : PLAYER_2_MASK | board];
	}

	public static boolean hasPossibleWins(int board, int otherPlayer) {
		return hasPossibleWins[otherPlayer == TwoPlayers.PLAYER_1 ? board : PLAYER_2_MASK | board];
	}

	public static int[] getDynamicMoves(int board, int player) {
		return dynamicMoves[player == TwoPlayers.PLAYER_1 ? board : PLAYER_2_MASK | board];
	}

	public static int[] getQuietMoves(int board, int player) {
		return quietMoves[player == TwoPlayers.PLAYER_1 ? board : PLAYER_2_MASK | board];
	}
}
