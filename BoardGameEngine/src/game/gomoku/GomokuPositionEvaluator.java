package game.gomoku;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;

public class GomokuPositionEvaluator implements IPositionEvaluator<Coordinate, GomokuPosition> {
	private static final int BOARD_WIDTH = GomokuPosition.BOARD_WIDTH;

	@Override
	public double evaluate(GomokuPosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (winExists(position.board, lastPlayer)) {
			return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
		}
		// XXX check draw?
		int opponent = TwoPlayers.otherPlayer(player);
		return score(position.board, player, opponent) - score(position.board, opponent, player);
	}

	private int score(int[][] board, int player, int opponent) {
		int[] open = new int[4];
		int[] closed = new int[4];
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (board[y][x] == player) {
					int left = x > 0 ? board[y][x - 1] : opponent;
					int upLeft = x > 0 && y > 0 ? board[y - 1][x - 1] : opponent;
					int up = y > 0 ? board[y - 1][x] : opponent;
					int upRight = x < BOARD_WIDTH - 1 && y > 0 ? board[y - 1][x + 1] : opponent;
					int rightCount = 0;
					while (x < BOARD_WIDTH - (rightCount + 1) && board[y][x + (rightCount + 1)] == player) {
						++rightCount;
					}
					int downRightCount = 0;
					while (x < BOARD_WIDTH - (downRightCount + 1) && y < BOARD_WIDTH - (downRightCount + 1) && board[y + (downRightCount + 1)][x + (downRightCount + 1)] == player) {
						++downRightCount;
					}
					int downCount = 0;
					while (y < BOARD_WIDTH - (downCount + 1) && board[y + (downCount + 1)][x] == player) {
						++downCount;
					}
					int downLeftCount = 0;
					while (x > downLeftCount && y < BOARD_WIDTH - (downLeftCount + 1) && board[y + (downLeftCount + 1)][x - (downLeftCount + 1)] == player) {
						++downLeftCount;
					}
					int nextRight = x < BOARD_WIDTH - 1 ? board[y][x + 1] : opponent;
					int nextDownRight = x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 ? board[y + 1][x + 1] : opponent;
					int nextDown = y < BOARD_WIDTH - 1 ? board[y + 1][x] : opponent;
					int nextDownLeft = x > 0 && y < BOARD_WIDTH - 1 ? board[y + 1][x - 1] : opponent;
					if (rightCount == 0 && downRightCount == 0 && downCount == 0 && downLeftCount == 0) {
						if (left == TwoPlayers.UNPLAYED && upLeft == TwoPlayers.UNPLAYED && up == TwoPlayers.UNPLAYED && upRight == TwoPlayers.UNPLAYED &&
								nextRight == TwoPlayers.UNPLAYED && nextDownRight == TwoPlayers.UNPLAYED && nextDown == TwoPlayers.UNPLAYED && nextDownLeft == TwoPlayers.UNPLAYED) {
							++open[0];
						} else if (left == TwoPlayers.UNPLAYED || upLeft == TwoPlayers.UNPLAYED || up == TwoPlayers.UNPLAYED || upRight == TwoPlayers.UNPLAYED ||
								nextRight == TwoPlayers.UNPLAYED || nextDownRight == TwoPlayers.UNPLAYED || nextDown == TwoPlayers.UNPLAYED || nextDownLeft == TwoPlayers.UNPLAYED) {
							++closed[0];
						}
						continue;
					}
					if (left == TwoPlayers.UNPLAYED && nextRight == TwoPlayers.UNPLAYED) {
						++open[rightCount];
					} else if (left == TwoPlayers.UNPLAYED || nextRight == TwoPlayers.UNPLAYED) {
						if (left != player) {
							++closed[rightCount];
						}
					}
					if (upLeft == TwoPlayers.UNPLAYED && nextDownRight == TwoPlayers.UNPLAYED) {
						++open[downRightCount];
					} else if (upLeft == TwoPlayers.UNPLAYED || nextDownRight == TwoPlayers.UNPLAYED) {
						if (upLeft != player) {
							++closed[downRightCount];
						}
					}
					if (up == TwoPlayers.UNPLAYED && nextDown == TwoPlayers.UNPLAYED) {
						++open[downCount];
					} else if (up == TwoPlayers.UNPLAYED || nextDown == TwoPlayers.UNPLAYED) {
						if (up != player) {
							++closed[downCount];
						}
					}
					if (upRight == TwoPlayers.UNPLAYED && nextDownLeft == TwoPlayers.UNPLAYED) {
						++open[downLeftCount];
					} else if (upRight == TwoPlayers.UNPLAYED || nextDownLeft == TwoPlayers.UNPLAYED) {
						if (upRight != player) {
							++closed[downLeftCount];
						}
					}
				}
			}
		}
		return 16 * open[3] + 8 * (closed[3] + open[2]) + 4 * (closed[3] + open[2]) + 2 * (closed[1] + open[0]) + closed[0];
	}

	static boolean winExists(int[][] board, int player) {
		for (int y = 0; y < BOARD_WIDTH; ++y) {
			for (int x = 0; x < BOARD_WIDTH; ++x) {
				if (board[y][x] == player) {
					if (x < BOARD_WIDTH - 4 && board[y][x + 1] == player && board[y][x + 2] == player && board[y][x + 3] == player && board[y][x + 4] == player) {
						return true;
					}
					if (y < BOARD_WIDTH - 4 && board[y + 1][x] == player && board[y + 2][x] == player && board[y + 3][x] == player && board[y + 4][x] == player) {
						return true;
					}
					if (x < BOARD_WIDTH - 4 && y < BOARD_WIDTH - 4 && board[y + 1][x + 1] == player && board[y + 2][x + 2] == player && board[y + 3][x + 3] == player
							&& board[y + 4][x + 4] == player) {
						return true;
					}
					if (x > 3 && y < BOARD_WIDTH - 4 && board[y + 1][x - 1] == player && board[y + 2][x - 2] == player && board[y + 3][x - 3] == player && board[y + 4][x - 4] == player) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public IPositionEvaluator<Coordinate, GomokuPosition> createCopy() {
		return new GomokuPositionEvaluator();
	}
}
