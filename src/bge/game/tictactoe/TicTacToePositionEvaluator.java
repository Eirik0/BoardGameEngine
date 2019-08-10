package bge.game.tictactoe;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.Coordinate;
import bge.game.MoveList;
import bge.game.TwoPlayers;

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
