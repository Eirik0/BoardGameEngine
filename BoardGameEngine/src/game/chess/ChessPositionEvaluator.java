package game.chess;

import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;
import game.chess.move.IChessMove;

public class ChessPositionEvaluator implements IPositionEvaluator<IChessMove, ChessPosition> {
	@Override
	public double evaluate(ChessPosition position, int player) {
		int opponent = TwoPlayers.otherPlayer(player);
		Coordinate playerKingSquare = position.kingSquares[player];
		if (position.getPossibleMoves().isEmpty()) {
			if (ChessFunctions.isSquareAttacked(position, playerKingSquare.x, playerKingSquare.y, opponent)) {
				return Double.NEGATIVE_INFINITY;
			} else {
				return 0;
			}
		}
		return position.materialScore[player] - position.materialScore[opponent];
	}
}
