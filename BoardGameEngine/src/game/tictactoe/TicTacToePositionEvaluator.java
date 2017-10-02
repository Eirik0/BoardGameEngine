package game.tictactoe;

import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.board, lastPlayer)) {
			if (player == lastPlayer) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		} else {
			return 0;
		}
	}
}
