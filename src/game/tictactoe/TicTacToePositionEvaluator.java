package game.tictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, MoveList<Coordinate> possibleMoves) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.board, lastPlayer)) {
			return AnalysisResult.LOSS;
		} else {
			return possibleMoves.size() == 0 ? AnalysisResult.DRAW : 0;
		}
	}
}
