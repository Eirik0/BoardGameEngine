package game.tictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.ArrayMoveList;
import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;

public class TicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, TicTacToePosition> {
	private final MoveList<Coordinate> moveList = new ArrayMoveList<>(TicTacToeGame.MAX_MOVES);

	@Override
	public double evaluate(TicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.board, lastPlayer)) {
			return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
		} else {
			moveList.clear();
			position.getPossibleMoves(moveList);
			return moveList.size() == 0 ? AnalysisResult.DRAW : 0;
		}
	}

	@Override
	public IPositionEvaluator<Coordinate, TicTacToePosition> createCopy() {
		return new TicTacToePositionEvaluator();
	}
}
