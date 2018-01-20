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
		int[] playerOpen = new int[4];
		int[] playerClosed = new int[4];
		int[] opponentOpen = new int[4];
		int[] opponentClosed = new int[4];
		score(position.board, position.currentPlayer, opponent, playerOpen, playerClosed);
		score(position.board, opponent, position.currentPlayer, opponentOpen, opponentClosed);
		int playerScore = 16 * playerOpen[3] + 8 * (playerClosed[3] + playerOpen[2]) + 4 * (playerClosed[3] + playerOpen[2]) + 2 * playerClosed[1];
		int opponentScore = 16 * opponentOpen[3] + 8 * (opponentClosed[3] + opponentOpen[2]) + 4 * (opponentClosed[3] + opponentOpen[2]) + 2 * opponentClosed[1];
		if (playerOpen[3] > 0 || playerClosed[3] > 0) {
			return AnalysisResult.WIN_INT - 1;
		}
		return playerScore - opponentScore;
	}

	public static void score(int[] board, int player, int opponent, int open[], int closed[]) {
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
						boolean closedStart = i - dir < GomokuUtilities.START_BOARD_INDEX || board[i - dir] == opponent || (i - dir) % 20 == 0;
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
	}
}
