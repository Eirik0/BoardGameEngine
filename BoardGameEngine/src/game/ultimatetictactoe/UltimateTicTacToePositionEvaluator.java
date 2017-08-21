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
			return 0;
		}
	}
}
