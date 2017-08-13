package game.tictactoe;

import analysis.IPositionEvaluator;
import game.Coordinate;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, int player) {
		if (TicTacToePosition.winsExist(position.board, player)) {
			return Double.POSITIVE_INFINITY;
		} else if (TicTacToePosition.winsExist(position.board, TicTacToePosition.otherPlayer(player))) {
			return Double.NEGATIVE_INFINITY;
		} else {
			return 0;
		}
	}
}
