package game.gomoku;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.MoveList;
import game.TwoPlayers;

public class GomokuPositionEvaluator implements IPositionEvaluator<Integer, GomokuPosition> {
	@Override
	public double evaluate(GomokuPosition position, MoveList<Integer> possibleMoves) {
		int opponent = TwoPlayers.otherPlayer(position.currentPlayer);
		if (position.gameOver) {
			return AnalysisResult.LOSS;
		} else if (possibleMoves.size() == 0) {
			return AnalysisResult.DRAW;
		}
		return score(position.board, position.currentPlayer, opponent) - score(position.board, opponent, position.currentPlayer);
	}

	private static int score(int[] board, int player, int opponent) {
		int[] open = new int[4];
		int[] closed = new int[4];
		int i = GomokuUtilities.START_BOARD_INDEX;
		do {
			int j = 0;
			do {
				if (board[i] == player) {
					int dirIndex = 4;
					do {
						int inARow = 1;
						int dir = GomokuUtilities.DIRECTIONS[dirIndex];
						if (board[i - dir] == player) {
							continue;
						}
						int pos = i + dir;
						while (pos < GomokuUtilities.FINAL_BOARD_INDEX) {
							if (board[pos] == player) {
								++inARow;
							} else {
								break;
							}
							pos += dir;
						}
						boolean closedStart = i - dir < GomokuUtilities.START_BOARD_INDEX || board[i - dir] == opponent || i % 20 == 0;
						boolean closedEnd = pos > GomokuUtilities.FINAL_BOARD_INDEX || board[pos] == opponent || pos % 20 == 0;
						if (closedStart && closedEnd) {
							continue;
						} else if (closedStart || closedEnd) {
							++closed[inARow - 1];
						} else {
							++open[inARow - 1];
						}
					} while (++dirIndex < GomokuUtilities.NUM_DIRECTIONS);
				}
				++i;
			} while (++j < GomokuUtilities.BOARD_WIDTH);
		} while (++i < GomokuUtilities.FINAL_BOARD_INDEX);
		return 16 * open[3] + 8 * (closed[3] + open[2]) + 4 * (closed[3] + open[2]) + 2 * (closed[1] + open[0]) + closed[0];
	}
}
