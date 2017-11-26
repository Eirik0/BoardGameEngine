package game.chess;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.ArrayMoveList;
import game.MoveList;
import game.TwoPlayers;
import game.chess.move.IChessMove;

public class ChessPositionEvaluator implements IPositionEvaluator<IChessMove, ChessPosition> {
	MoveList<IChessMove> moveList = new ArrayMoveList<>(ChessGame.MAX_MOVES);

	@Override
	public double evaluate(ChessPosition position, int player) {
		moveList.clear();
		position.getPossibleMoves(moveList);
		if (moveList.size() == 0) {
			int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
			int playerKingSquare = position.kingSquares[position.currentPlayer];
			if (position.halfMoveClock < 100 && ChessFunctions.isSquareAttacked(position, playerKingSquare, lastPlayer)) {
				return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
			} else {
				return AnalysisResult.DRAW;
			}
		}
		return position.materialScore[player] - position.materialScore[TwoPlayers.otherPlayer(player)];
	}

	@Override
	public IPositionEvaluator<IChessMove, ChessPosition> createCopy() {
		return new ChessPositionEvaluator();
	}
}
