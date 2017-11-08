package game.chess;

import game.TwoPlayers;

public class ChessFunctions implements ChessConstants {
	public static boolean isSquareAttacked(ChessPosition position, int x, int y, int opponent) {
		boolean white = opponent == TwoPlayers.PLAYER_2;
		int opponentPawn = opponent | PAWN;
		int opponentKnight = opponent | KNIGHT;
		int opponentBishop = opponent | BISHOP;
		int opponentRook = opponent | ROOK;
		int opponentQueen = opponent | QUEEN;
		int opponentKing = opponent | KING;
		return isAttackedByPawn(position, x, y, opponentPawn, white) ||
				isAttackedByKnight(position, x, y, opponentKnight) ||
				isAttackedByQueenOrBishop(position, x, y, opponentQueen, opponentBishop) ||
				isAttackedByQueenOrRook(position, x, y, opponentQueen, opponentRook) ||
				isAttackedByKing(position, x, y, opponentKing);
	}

	private static boolean isAttackedByPawn(ChessPosition position, int x, int y, int opponentPawn, boolean white) {
		if (white) {
			return (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 1] == opponentPawn) || // capture right
					(x > 0 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 1] == opponentPawn);// capture left
		} else {
			return (x < BOARD_WIDTH - 1 && y > 0 && position.squares[y - 1][x + 1] == opponentPawn) || // capture right
					(x > 0 && y > 0 && position.squares[y - 1][x - 1] == opponentPawn);// capture left
		}
	}

	private static boolean isAttackedByKnight(ChessPosition position, int x, int y, int opponentKnight) {
		// clockwise starting upper left
		return (x > 0 && y > 1 && position.squares[y - 2][x - 1] == opponentKnight) ||
				(x < BOARD_WIDTH - 1 && y > 1 && position.squares[y - 2][x + 1] == opponentKnight) ||
				(x < BOARD_WIDTH - 2 && y > 1 && position.squares[y - 1][x + 2] == opponentKnight) ||
				(x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 2] == opponentKnight) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && position.squares[y + 2][x + 1] == opponentKnight) ||
				(x > 0 && y < BOARD_WIDTH - 2 && position.squares[y + 2][x - 1] == opponentKnight) ||
				(x > 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 2] == opponentKnight) ||
				(x > 1 && y > 0 && position.squares[y - 1][x - 2] == opponentKnight);
	}

	private static boolean isAttackedByQueenOrBishop(ChessPosition position, int x, int y, int opponentQueen, int opponentBishop) {
		int bx = x;
		int by = y;
		while (bx > 0 && by > 0 && position.squares[--by][--bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && position.squares[--by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && position.squares[++by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && position.squares[++by][--bx] == UNPLAYED) {
		}
		return position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentBishop;
	}

	private static boolean isAttackedByQueenOrRook(ChessPosition position, int x, int y, int opponentQueen, int opponentRook) {
		int bx = x;
		int by = y;
		while (by > 0 && position.squares[--by][bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && position.squares[by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentRook) {
			return true;
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && position.squares[++by][bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && position.squares[by][--bx] == UNPLAYED) {
		}
		return position.squares[by][bx] == opponentQueen || position.squares[by][bx] == opponentRook;
	}

	private static boolean isAttackedByKing(ChessPosition position, int x, int y, int opponentKing) {
		// clockwise starting upper left
		return (x > 0 && y > 0 && position.squares[y - 1][x - 1] == opponentKing) ||
				(y > 0 && position.squares[y - 1][x] == opponentKing) ||
				(x < BOARD_WIDTH - 1 && y > 0 && position.squares[y - 1][x + 1] == opponentKing) ||
				(x < BOARD_WIDTH - 1 && position.squares[y][x + 1] == opponentKing) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 1] == opponentKing) ||
				(y < BOARD_WIDTH - 1 && position.squares[y + 1][x] == opponentKing) ||
				(x > 0 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 1] == opponentKing) ||
				(x > 0 && position.squares[y][x - 1] == opponentKing);
	}

	public static double getPieceScore(int piece) {
		switch (piece & ALL_PIECES) {
		case 0:
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
			return 0;
		}
	}
}
