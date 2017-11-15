package game.chess;

import game.TwoPlayers;

public class ChessFunctions implements ChessConstants {
	public static boolean isSquareAttacked(ChessPosition position, int square, int opponent) {
		boolean white = opponent == TwoPlayers.PLAYER_2;
		int opponentPawn = opponent | PAWN;
		int opponentKnight = opponent | KNIGHT;
		int opponentBishop = opponent | BISHOP;
		int opponentRook = opponent | ROOK;
		int opponentQueen = opponent | QUEEN;
		int opponentKing = opponent | KING;
		return isAttackedSliding(position, square, opponentBishop, opponentQueen, BISHOP_OFFSETS) ||
				isAttackedSliding(position, square, opponentRook, opponentQueen, ROOK_OFFSETS) ||
				isAttackedNonSliding(position, square, opponentKnight, KNIGHT_OFFSETS) ||
				isAttackedNonSliding(position, square, opponentKing, KING_OFFSETS) ||
				isAttackedByPawn(position, square, opponentPawn, white);
	}

	private static boolean isAttackedByPawn(ChessPosition position, int square, int opponentPawn, boolean white) {
		if (white) {
			return (position.squares[square + 9] == opponentPawn) || // capture right
					(position.squares[square + 11] == opponentPawn);// capture left
		} else {
			return (position.squares[square - 11] == opponentPawn) || // capture right
					(position.squares[square - 9] == opponentPawn);// capture left
		}
	}

	private static boolean isAttackedNonSliding(ChessPosition position, int square, int opponentPiece, int[] offsets) {
		for (int i = 0; i < offsets.length; ++i) {
			if (position.squares[square + offsets[i]] == opponentPiece) {
				return true;
			}
		}
		return false;
	}

	private static boolean isAttackedSliding(ChessPosition position, int square, int opponentRookOrBishop, int opponentQueen, int[] offsets) {
		for (int i = 0; i < offsets.length; ++i) {
			int offset = offsets[i];
			int to = square + offset;
			int piece;
			while ((piece = position.squares[to]) == UNPLAYED) {
				to += offset;
			}
			if (piece == opponentRookOrBishop || piece == opponentQueen) {
				return true;
			}
		}
		return false;
	}

	public static double getPieceScore(int piece) {
		switch (piece & ALL_PIECES) {
		case UNPLAYED:
			return 0;
		case PAWN:
			return PAWN_SCORE;
		case KNIGHT:
			return KNIGHT_SCORE;
		case BISHOP:
			return BISHOP_SCORE;
		case ROOK:
			return ROOK_SCORE;
		case QUEEN:
			return QUEEN_SCORE;
		default:
			throw new IllegalStateException("Unknown piece: " + piece);
		}
	}
}
