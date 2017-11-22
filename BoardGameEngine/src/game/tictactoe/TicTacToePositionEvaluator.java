package game.tictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.board, lastPlayer)) {
			return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
		} else {
			return position.getPossibleMoves().isEmpty() ? AnalysisResult.DRAW : 0;
		}
	}
}
