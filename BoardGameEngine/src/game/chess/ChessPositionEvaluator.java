package game.chess;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.MoveList;
import game.TwoPlayers;
import game.chess.move.IChessMove;

public class ChessPositionEvaluator implements IPositionEvaluator<IChessMove, ChessPosition>, ChessEvaluationConstants {
	@Override
	public double evaluate(ChessPosition position, MoveList<IChessMove> possibleMoves) {
		if (possibleMoves.size() == 0) {
			int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
			int playerKingSquare = position.kingSquares[position.currentPlayer];
			if (position.halfMoveClock < 100 && ChessFunctions.isSquareAttacked(position, playerKingSquare, lastPlayer)) {
				return AnalysisResult.LOSS;
			} else {
				return AnalysisResult.DRAW;
			}
		}
		return score(position, position.currentPlayer) - score(position, position.otherPlayer);
	}

	private static double score(ChessPosition position, int player) {
		double score = position.materialScore[player];
		int i = 0;
		while (i < position.numPawns[player]) {
			score += PAWN_SCORES[player][position.pawns[player][i]];
			++i;
		}
		i = 0;
		while (i < position.numKnights[player]) {
			score += KNIGHT_SCORES[player][position.knights[player][i]];
			++i;
		}
		i = 0;
		while (i < position.numBishops[player]) {
			score += BISHOP_SCORES[player][position.bishops[player][i]];
			++i;
		}
		i = 0;
		while (i < position.numRooks[player]) {
			score += ROOK_SCORES[player][position.rooks[player][i]];
			++i;
		}
		i = 0;
		while (i < position.numQueens[player]) {
			score += QUEEN_SCORES[player][position.queens[player][i]];
			++i;
		}
		score += KING_SCORES[player][position.kingSquares[player]];
		return score;
	}
}
