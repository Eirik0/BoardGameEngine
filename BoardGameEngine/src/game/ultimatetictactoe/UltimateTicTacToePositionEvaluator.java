package game.ultimatetictactoe;

import game.tictactoe.TicTacToePosition;
import analysis.IPositionEvaluator;

public class UltimateTicTacToePositionEvaluator implements IPositionEvaluator<UTTTCoordinate, UltimateTicTacToePosition> {
	@Override
	public double evaluate(UltimateTicTacToePosition position, int player) {
		if (UltimateTicTacToeUtilities.winsExist(position.wonBoards, player)) {
			return Double.POSITIVE_INFINITY;
		} else if (UltimateTicTacToeUtilities.winsExist(position.wonBoards, TicTacToePosition.otherPlayer(player))) {
			return Double.NEGATIVE_INFINITY;
		} else {
			int wins = 0;
			int losses = 0;
			int otherPlayer = TicTacToePosition.otherPlayer(player);
			for (int i = 0; i < position.wonBoards.length; ++i) {
				if (position.wonBoards[i] == player) {
					++wins;
				} else if (position.wonBoards[i] == otherPlayer) {
					++losses;
				}
			}
			return wins - losses;
		}
	}
}
