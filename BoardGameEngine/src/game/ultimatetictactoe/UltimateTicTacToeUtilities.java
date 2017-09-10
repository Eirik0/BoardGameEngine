package game.ultimatetictactoe;

import game.Coordinate;

public class UltimateTicTacToeUtilities {
	//((0, 0), (0, 1), (0, 2), (1, 0), (1, 1), (1, 2), (2, 0), (2, 1), (2, 2),
	// (0, 3), (0, 4), (0, 5), (1, 3), (1, 4), (1, 5), (2, 3), (2, 4), (2, 5),
	// (0, 6), (0, 7), (0, 8), (1, 6), (1, 7), (1, 8), (2, 6), (2, 7), (2, 8),
	// (3, 0), (3, 1), (3, 2), (4, 0), (4, 1), (4, 2), (5, 0), (5, 1), (5, 2),
	// (3, 3), (3, 4), (3, 5), (4, 3), (4, 4), (4, 5), (5, 3), (5, 4), (5, 5),
	// (3, 6), (3, 7), (3, 8), (4, 6), (4, 7), (4, 8), (5, 6), (5, 7), (5, 8),
	// (6, 0), (6, 1), (6, 2), (7, 0), (7, 1), (7, 2), (8, 0), (8, 1), (8, 2),
	// (6, 3), (6, 4), (6, 5), (7, 3), (7, 4), (7, 5), (8, 3), (8, 4), (8, 5),
	// (6, 6), (6, 7), (6, 8), (7, 6), (7, 7), (7, 8), (8, 6), (8, 7), (8, 8))

	private static final Coordinate[] BOARD_NM = new Coordinate[] {
			nm(0, 0), nm(0, 1), nm(0, 2), nm(1, 0), nm(1, 1), nm(1, 2), nm(2, 0), nm(2, 1), nm(2, 2),
			nm(0, 3), nm(0, 4), nm(0, 5), nm(1, 3), nm(1, 4), nm(1, 5), nm(2, 3), nm(2, 4), nm(2, 5),
			nm(0, 6), nm(0, 7), nm(0, 8), nm(1, 6), nm(1, 7), nm(1, 8), nm(2, 6), nm(2, 7), nm(2, 8),
			nm(3, 0), nm(3, 1), nm(3, 2), nm(4, 0), nm(4, 1), nm(4, 2), nm(5, 0), nm(5, 1), nm(5, 2),
			nm(3, 3), nm(3, 4), nm(3, 5), nm(4, 3), nm(4, 4), nm(4, 5), nm(5, 3), nm(5, 4), nm(5, 5),
			nm(3, 6), nm(3, 7), nm(3, 8), nm(4, 6), nm(4, 7), nm(4, 8), nm(5, 6), nm(5, 7), nm(5, 8),
			nm(6, 0), nm(6, 1), nm(6, 2), nm(7, 0), nm(7, 1), nm(7, 2), nm(8, 0), nm(8, 1), nm(8, 2),
			nm(6, 3), nm(6, 4), nm(6, 5), nm(7, 3), nm(7, 4), nm(7, 5), nm(8, 3), nm(8, 4), nm(8, 5),
			nm(6, 6), nm(6, 7), nm(6, 8), nm(7, 6), nm(7, 7), nm(7, 8), nm(8, 6), nm(8, 7), nm(8, 8) };

	private static Coordinate nm(int n, int m) {
		return Coordinate.valueOf(n, m);
	}

	public static Coordinate getBoardXY(int n, int m) {
		Coordinate intersection = BOARD_NM[n * UltimateTicTacToePosition.BOARD_WIDTH + m];
		return Coordinate.valueOf(intersection.y, intersection.x);
	}

	public static Coordinate getBoardNM(int x, int y) {
		return BOARD_NM[y * UltimateTicTacToePosition.BOARD_WIDTH + x];
	}

	public static boolean winsExist(int[] cells, int player) {
		// win with middle
		if (cells[4] == player) {
			if (cells[0] == player && cells[8] == player) {
				return true;
			}
			if (cells[2] == player && cells[6] == player) {
				return true;
			}
			if (cells[1] == player && cells[7] == player) {
				return true;
			}
			if (cells[3] == player && cells[5] == player) {
				return true;
			}
		}
		// win with upper left but not middle
		if (cells[0] == player) {
			if (cells[1] == player && cells[2] == player) {
				return true;
			}
			if (cells[3] == player && cells[6] == player) {
				return true;
			}
		}
		// win with bottom right but not middle (or upper left)
		if (cells[8] == player) {
			if (cells[2] == player && cells[5] == player) {
				return true;
			}
			if (cells[6] == player && cells[7] == player) {
				return true;
			}
		}
		return false;
	}

	public static int countPossibleWins(int[] board, int otherPlayer) {
		// in testing, this proved faster than first checking if the center is captured and then doing whatever
		boolean has0 = board[0] != otherPlayer;
		boolean has1 = board[1] != otherPlayer;
		boolean has2 = board[2] != otherPlayer;
		boolean has3 = board[3] != otherPlayer;
		boolean has4 = board[4] != otherPlayer;
		boolean has5 = board[5] != otherPlayer;
		boolean has6 = board[6] != otherPlayer;
		boolean has7 = board[7] != otherPlayer;
		boolean has8 = board[8] != otherPlayer;
		return (has0 && has1 && has2 ? 1 : 0) +
				(has3 && has4 && has5 ? 1 : 0) +
				(has6 && has7 && has8 ? 1 : 0) +
				(has0 && has3 && has6 ? 1 : 0) +
				(has1 && has4 && has7 ? 1 : 0) +
				(has2 && has5 && has8 ? 1 : 0) +
				(has0 && has4 && has8 ? 1 : 0) +
				(has2 && has4 && has6 ? 1 : 0);
	}
}
