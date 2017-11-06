package game.chess;

public class ChessFunctions implements ChessConstants {
	public static boolean isSquareAttacked(ChessPosition position, int x, int y, int otherPlayer) {
		int otherPawn = otherPlayer | PAWN;
		int otherKnight = otherPlayer | KNIGHT;
		int otherBishop = otherPlayer | BISHOP;
		int otherRook = otherPlayer | ROOK;
		int otherQueen = otherPlayer | QUEEN;
		int otherKing = otherPlayer | KING;
		return isAttackedByPawn(position, x, y, otherPawn) ||
				isAttackedByKnight(position, x, y, otherKnight) ||
				isAttackedByQueenOrBishop(position, x, y, otherQueen, otherBishop) ||
				isAttackedByQueenOrRook(position, x, y, otherQueen, otherRook) ||
				isAttackedByKing(position, x, y, otherKing);
	}

	private static boolean isAttackedByPawn(ChessPosition position, int x, int y, int otherPawn) {
		if (position.white) {
			return (x < BOARD_WIDTH - 1 && y > 0 && position.squares[y - 1][x + 1] == otherPawn) || // capture right
					(x > 0 && y > 0 && position.squares[y - 1][x - 1] == otherPawn);// capture left
		} else {
			return (x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 1] == otherPawn) || // capture right
					(x > 0 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 1] == otherPawn);// capture left
		}
	}

	private static boolean isAttackedByKnight(ChessPosition position, int x, int y, int otherKnight) {
		// clockwise starting upper left
		return (x > 0 && y > 1 && position.squares[y - 2][x - 1] == otherKnight) ||
				(x < BOARD_WIDTH - 1 && y > 1 && position.squares[y - 2][x + 1] == otherKnight) ||
				(x < BOARD_WIDTH - 2 && y > 1 && position.squares[y - 1][x + 2] == otherKnight) ||
				(x < BOARD_WIDTH - 2 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 2] == otherKnight) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 2 && position.squares[y + 2][x + 1] == otherKnight) ||
				(x > 0 && y < BOARD_WIDTH - 2 && position.squares[y + 2][x - 1] == otherKnight) ||
				(x > 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 2] == otherKnight) ||
				(x > 1 && y > 0 && position.squares[y - 1][x - 2] == otherKnight);
	}

	private static boolean isAttackedByQueenOrBishop(ChessPosition position, int x, int y, int otherQueen, int otherBishop) {
		int bx = x;
		int by = y;
		while (bx > 0 && by > 0 && position.squares[--by][--bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by > 0 && position.squares[--by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && by < BOARD_WIDTH - 1 && position.squares[++by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherBishop) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && by < BOARD_WIDTH - 1 && position.squares[++by][--bx] == UNPLAYED) {
		}
		return position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherBishop;
	}

	private static boolean isAttackedByQueenOrRook(ChessPosition position, int x, int y, int otherQueen, int otherRook) {
		int bx = x;
		int by = y;
		while (by > 0 && position.squares[--by][bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx < BOARD_WIDTH - 1 && position.squares[by][++bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (by < BOARD_WIDTH - 1 && position.squares[++by][bx] == UNPLAYED) {
		}
		if (position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherRook) {
			return true;
		}
		bx = x;
		by = y;
		while (bx > 0 && position.squares[by][--bx] == UNPLAYED) {
		}
		return position.squares[by][bx] == otherQueen || position.squares[by][bx] == otherRook;
	}

	private static boolean isAttackedByKing(ChessPosition position, int x, int y, int otherKing) {
		// clockwise starting upper left
		return (x > 0 && y > 0 && position.squares[y - 1][x - 1] == otherKing) ||
				(y > 0 && position.squares[y - 1][x] == otherKing) ||
				(x < BOARD_WIDTH - 1 && y > 0 && position.squares[y - 1][x + 1] == otherKing) ||
				(x < BOARD_WIDTH - 1 && position.squares[y][x + 1] == otherKing) ||
				(x < BOARD_WIDTH - 1 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x + 1] == otherKing) ||
				(y < BOARD_WIDTH - 1 && position.squares[y + 1][x] == otherKing) ||
				(x > 0 && y < BOARD_WIDTH - 1 && position.squares[y + 1][x - 1] == otherKing) ||
				(x > 0 && position.squares[y][x - 1] == otherKing);
	}
}
