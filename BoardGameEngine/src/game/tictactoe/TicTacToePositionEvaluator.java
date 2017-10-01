package game.tictactoe;

import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, int player) {
		if (TicTacToeUtilities.winsExist(position.board, player)) {
			return Double.POSITIVE_INFINITY;
		} else if (TicTacToeUtilities.winsExist(position.board, TwoPlayers.otherPlayer(player))) {
			return Double.NEGATIVE_INFINITY;
		} else {
			return 0;
		}
	}
}
