package game.ultimatetictactoe;

import analysis.IPositionEvaluator;
import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;

public class UltimateTicTacToePositionEvaluator implements IPositionEvaluator<UTTTCoordinate, UltimateTicTacToePosition> {
	private static final double SCORE_PER_BOARD = 8.0; // The number of three-in-a-rows
	private static final double TOTAL_SCORE = SCORE_PER_BOARD * UltimateTicTacToePosition.BOARD_WIDTH;

	@Override
	public double evaluate(UltimateTicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		boolean winsExist = TicTacToeUtilities.winExists(position.wonBoards, lastPlayer);
		if (winsExist) {
			if (player == lastPlayer) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		} else {
			int opponent = TwoPlayers.otherPlayer(player);
			int possibleWins = UltimateTicTacToeUtilities.countPossibleWins(position.wonBoards, opponent);
			int possibleLosses = UltimateTicTacToeUtilities.countPossibleWins(position.wonBoards, player);
			if (possibleWins == 0 && possibleLosses == 0) {
				return 0;
			}

			int[] possibleWinsByBoard = countPossibleWinsByBoard(position, player, opponent);
			int[] possibleLossesByBoard = countPossibleWinsByBoard(position, opponent, player);

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

	private int[] countPossibleWinsByBoard(UltimateTicTacToePosition position, int currentPlayer, int otherPlayer) {
		int[] possibleWinsByBoard = new int[UltimateTicTacToePosition.BOARD_WIDTH + 1]; // + 1 because we store the total in wonBoards[BOARD_WIDTH + 1]
		int totalPossibleWins = 0;
		for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
			int countPossibleWins;
			int wonBoardInt = (position.wonBoards >> (i << 1)) & TwoPlayers.BOTH_PLAYERS;
			if (wonBoardInt == otherPlayer) {
				countPossibleWins = 0;
			} else if (wonBoardInt == currentPlayer) {
				countPossibleWins = 8;
			} else {
				countPossibleWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[i], otherPlayer);
			}
			totalPossibleWins += countPossibleWins;
			possibleWinsByBoard[i] = countPossibleWins;
		}
		possibleWinsByBoard[UltimateTicTacToePosition.BOARD_WIDTH] = totalPossibleWins;
		return possibleWinsByBoard;
	}

	private int countActualPossibleWins(int[] possibleWinsByBoard, int player, int opponent) {
		int wonBoards0 = (possibleWinsByBoard[0] > 0 ? player : opponent) << 0;
		int wonBoards1 = (possibleWinsByBoard[1] > 0 ? player : opponent) << 2;
		int wonBoards2 = (possibleWinsByBoard[2] > 0 ? player : opponent) << 4;
		int wonBoards3 = (possibleWinsByBoard[3] > 0 ? player : opponent) << 6;
		int wonBoards4 = (possibleWinsByBoard[4] > 0 ? player : opponent) << 8;
		int wonBoards5 = (possibleWinsByBoard[5] > 0 ? player : opponent) << 10;
		int wonBoards6 = (possibleWinsByBoard[6] > 0 ? player : opponent) << 12;
		int wonBoards7 = (possibleWinsByBoard[7] > 0 ? player : opponent) << 14;
		int wonBoards8 = (possibleWinsByBoard[8] > 0 ? player : opponent) << 16;
		int wonBoards = wonBoards0 | wonBoards1 | wonBoards2 | wonBoards3 | wonBoards4 | wonBoards5 | wonBoards6 | wonBoards7 | wonBoards8;
		return UltimateTicTacToeUtilities.countPossibleWins(wonBoards, opponent);
	}
}
