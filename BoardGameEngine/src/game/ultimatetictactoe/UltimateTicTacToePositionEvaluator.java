package game.ultimatetictactoe;

import analysis.IPositionEvaluator;

public class UltimateTicTacToePositionEvaluator implements IPositionEvaluator<UTTTCoordinate, UltimateTicTacToePosition> {
	private static final double SCORE_PER_BOARD = 8.0; // The number of three-in-a-rows
	private static final double TOTAL_SCORE = SCORE_PER_BOARD * UltimateTicTacToePosition.BOARD_WIDTH;

	@Override
	public double evaluate(UltimateTicTacToePosition position, int player) {
		int opponent = player == UltimateTicTacToePosition.PLAYER_1 ? UltimateTicTacToePosition.PLAYER_2 : UltimateTicTacToePosition.PLAYER_1;
		if (UltimateTicTacToeUtilities.winsExist(position.wonBoards, player)) {
			return Double.POSITIVE_INFINITY;
		} else if (UltimateTicTacToeUtilities.winsExist(position.wonBoards, opponent)) {
			return Double.NEGATIVE_INFINITY;
		} else {
			int possibleWins = UltimateTicTacToeUtilities.countPossibleWins(position.wonBoards, opponent);
			int possibleLosses = UltimateTicTacToeUtilities.countPossibleWins(position.wonBoards, player);
			if (possibleWins == 0 && possibleLosses == 0) {
				return 0;
			}

			int[] possibleWinsByBoard = countPossibleWinsByBoard(position, opponent);
			int[] possibleLossesByBoard = countPossibleWinsByBoard(position, player);

			int actualPossibleWins = countActualPossibleWins(possibleWinsByBoard, player, opponent);
			int actualPossibleLosses = countActualPossibleWins(possibleLossesByBoard, opponent, player);

			if (actualPossibleWins == 0 && actualPossibleLosses == 0) {
				return 0;
			}

			double playerRatio = possibleWinsByBoard[UltimateTicTacToePosition.BOARD_WIDTH] / TOTAL_SCORE;
			double opponentRatio = possibleLossesByBoard[UltimateTicTacToePosition.BOARD_WIDTH] / TOTAL_SCORE;

			return playerRatio * actualPossibleWins - opponentRatio * actualPossibleLosses;
		}
	}

	private int[] countPossibleWinsByBoard(UltimateTicTacToePosition position, int otherPlayer) {
		int[] possibleWinsByBoard = new int[UltimateTicTacToePosition.BOARD_WIDTH + 1];// + 1 because we store the total in wonBoards[BOARD_WIDTH + 1]
		int totalPossibleWins = 0;
		for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
			int countPossibleWins = UltimateTicTacToeUtilities.countPossibleWins(position.cells[i], otherPlayer);
			totalPossibleWins += countPossibleWins;
			possibleWinsByBoard[i] = countPossibleWins;
		}
		possibleWinsByBoard[UltimateTicTacToePosition.BOARD_WIDTH] = totalPossibleWins;
		return possibleWinsByBoard;
	}

	private int countActualPossibleWins(int[] possibleWinsByBoard, int player, int opponent) {
		int[] wonBoards = new int[UltimateTicTacToePosition.BOARD_WIDTH];
		for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
			wonBoards[i] = possibleWinsByBoard[i] > 0 ? player : opponent;
		}
		return UltimateTicTacToeUtilities.countPossibleWins(wonBoards, opponent);
	}
}
