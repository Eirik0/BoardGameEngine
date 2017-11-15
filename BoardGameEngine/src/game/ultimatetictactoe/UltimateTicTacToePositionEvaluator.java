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
		if (TicTacToeUtilities.winExists(position.wonBoards, lastPlayer)) {
			return player == lastPlayer ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
		} else {
			int opponent = TwoPlayers.otherPlayer(player);
			if (UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards, opponent) || UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards, player)) {
				return scorePossibleWinsByBoard(position, player, opponent) - scorePossibleWinsByBoard(position, opponent, player);
			}
			return 0;
		}
	}

	private double scorePossibleWinsByBoard(UltimateTicTacToePosition position, int player, int opponent) {
		int totalPossibleWins = 0;
		int possibleWonBoards = position.wonBoards;
		int n = 0;
		while (n < UltimateTicTacToePosition.BOARD_WIDTH) {
			int wonBoardInt = (position.wonBoards >> (n << 1)) & TwoPlayers.BOTH_PLAYERS;
			if (wonBoardInt == TwoPlayers.UNPLAYED) {
				int countPossibleWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[n], opponent);
				totalPossibleWins += countPossibleWins;
				possibleWonBoards |= (countPossibleWins > 0 ? player : opponent) << (n << 1);
			} else if (wonBoardInt == player) {
				totalPossibleWins += 8;
			}
			++n;
		}
		int actualPossibleWins = UltimateTicTacToeUtilities.countPossibleWins(possibleWonBoards, opponent);
		return actualPossibleWins * (totalPossibleWins / TOTAL_SCORE);
	}
}
