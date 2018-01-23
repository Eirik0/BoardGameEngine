package game.ultimatetictactoe;

import java.util.ArrayList;
import java.util.List;

import game.tictactoe.TicTacToeUtilities;

public class UTTTProbabilityUtilities {
	static final WinCount[] WIN_COUNTS = new WinCount[TicTacToeUtilities.PLAYER_2_ALL_POS];

	static {
		WIN_COUNTS[0] = countWins(new TicTacBoard());
	}

	private static WinCount countWins(TicTacBoard ticTacBoard) {
		if (ticTacBoard.win == 1) {
			return new WinCount(1, 0, 0);
		} else if (ticTacBoard.win == 2) {
			return new WinCount(0, 0, 1);
		}

		List<TicTacMove> moves = ticTacBoard.getPossibleMoves();
		if (moves.size() == 0) {
			return new WinCount(0, 1, 0);
		}
		WinCount winCount = new WinCount(0, 0, 0);
		for (int i = 0; i < moves.size(); ++i) {
			TicTacMove move = moves.get(i);
			ticTacBoard.makeMove(move);
			int boardInt = ticTacBoard.getIntValue();
			if (WIN_COUNTS[boardInt] == null) {
				WinCount countWins = countWins(ticTacBoard);
				winCount.add(countWins);
				WIN_COUNTS[boardInt] = countWins;
			} else {
				winCount.add(WIN_COUNTS[boardInt]);
			}
			ticTacBoard.unmakeMove(move);
		}
		return winCount;
	}

	static class WinCount {
		int p1Wins;
		int draws;
		int p2Wins;

		WinCount(int p1Wins, int drawn, int p2Wins) {
			this.p1Wins = p1Wins;
			draws = drawn;
			this.p2Wins = p2Wins;
		}

		void add(WinCount countWins) {
			p1Wins += countWins.p1Wins;
			draws += countWins.draws;
			p2Wins += countWins.p2Wins;
		}

		int getTotal() {
			return p1Wins + draws + p2Wins;
		}

		public double getP1Probability() {
			return (double) p1Wins / getTotal();
		}

		public double getP2Probability() {
			return (double) p2Wins / getTotal();
		}

		@Override
		public String toString() {
			return "p1 " + p1Wins + ", p2 " + p2Wins + ", d " + draws;
		}
	}

	static class TicTacBoard {
		int[] board = new int[9];
		int win = 0;

		TicTacBoard() {

		}

		List<TicTacMove> getPossibleMoves() {
			List<TicTacMove> moves = new ArrayList<>();
			for (int i = 0; i < board.length; ++i) {
				if (board[i] == 0) {
					moves.add(new TicTacMove(i, 1));
					moves.add(new TicTacMove(i, 2));
				}
			}
			return moves;
		}

		void makeMove(TicTacMove move) {
			board[move.position] = move.player;
			if ((board[0] == move.player && board[1] == move.player && board[2] == move.player) ||
					(board[0] == move.player && board[1] == move.player && board[2] == move.player) ||
					(board[3] == move.player && board[4] == move.player && board[5] == move.player) ||
					(board[6] == move.player && board[7] == move.player && board[8] == move.player) ||
					(board[0] == move.player && board[3] == move.player && board[6] == move.player) ||
					(board[1] == move.player && board[4] == move.player && board[7] == move.player) ||
					(board[2] == move.player && board[5] == move.player && board[8] == move.player) ||
					(board[0] == move.player && board[4] == move.player && board[8] == move.player) ||
					(board[2] == move.player && board[4] == move.player && board[6] == move.player)) {
				win = move.player;
			}
		}

		void unmakeMove(TicTacMove move) {
			board[move.position] = 0;
			win = 0;
		}

		int getIntValue() {
			return board[0] | (board[1] << 2) | (board[2] << 4) | (board[3] << 6) | (board[4] << 8) | (board[5] << 10) | (board[6] << 12) | (board[7] << 14) | (board[8] << 16);
		}
	}

	static class TicTacMove {
		final int position;
		final int player;

		public TicTacMove(int position, int player) {
			this.position = position;
			this.player = player;
		}

		@Override
		public String toString() {
			return player + " " + position;
		}
	}
}
