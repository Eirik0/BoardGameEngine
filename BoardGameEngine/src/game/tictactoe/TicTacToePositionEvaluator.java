package game.tictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.ArrayMoveList;
import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	@Override
	public double evaluate(TicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.board, lastPlayer)) {
			return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
		} else {
			MoveList<Coordinate> possibleMoves = new ArrayMoveList<>(MoveList.MAX_SIZE);
			position.getPossibleMoves(possibleMoves);
			return possibleMoves.size() == 0 ? AnalysisResult.DRAW : 0;
		}
	}
}
