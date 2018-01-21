package game.papersoccer;

import game.Coordinate;

public class PaperSoccerUtilities {
	public static final int BOARD_WIDTH = 13;
	public static final int BOARD_SIZE = BOARD_WIDTH * BOARD_WIDTH;
	public static final int MAX_MOVES = 1024;
	public static final int MAX_REASONABLE_DEPTH = 256;

	//  1 2 3
	//   \|/
	// 8 -+- 4
	//   /|\
	//  7 6 5
	private static final int DIR_1 = 1 << 0;
	private static final int DIR_2 = 1 << 1;
	private static final int DIR_3 = 1 << 2;
	private static final int DIR_4 = 1 << 3;
	private static final int DIR_5 = 1 << 4;
	private static final int DIR_6 = 1 << 5;
	private static final int DIR_7 = 1 << 6;
	private static final int DIR_8 = 1 << 7;
	public static final int ALL_DIRS = DIR_1 | DIR_2 | DIR_3 | DIR_4 | DIR_5 | DIR_6 | DIR_7 | DIR_8;
	private static final int DIR_SIZE = 1 << 8;

	private static final int NUM_DIRS = 8;
	private static final int[] DIR_DELTAS = { -BOARD_WIDTH - 1, -BOARD_WIDTH, -BOARD_WIDTH + 1, 1, BOARD_WIDTH + 1, BOARD_WIDTH, BOARD_WIDTH - 1, -1 };
	private static final int[] DELTA_TO_DIRS = new int[] {
			DIR_1, DIR_2, DIR_3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			DIR_8, 0, DIR_4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			DIR_7, DIR_6, DIR_5
	};
	private static final int[] OPPOSITE_DELTA_TO_DIRS = new int[] {
			DIR_5, DIR_6, DIR_7, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			DIR_4, 0, DIR_8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			DIR_3, DIR_2, DIR_1
	};

	public static final int[][] DIRECTIONS_TAKEN = new int[DIR_SIZE][];
	public static final int[][] DIRECTIONS_REMAINING = new int[DIR_SIZE][];
	public static final Integer[] MOVES = new Integer[BOARD_SIZE];

	public static final int[] P1_GOAL_DISTANCE = new int[BOARD_SIZE];
	public static final int[] P2_GOAL_DISTANCE = new int[BOARD_SIZE];

	static {
		// Pre-calculate possible moves
		int i = 0;
		do {
			int size = 0;
			int dir = i;
			while (dir != 0) {
				if ((dir & 1) == 1) {
					++size;
				}
				dir >>= 1;
			}
			int[] directionsTaken = new int[size];
			int[] directionsRemaining = new int[NUM_DIRS - size];
			int takenIndex = 0;
			int remainingIndex = 0;
			dir = i;
			int j = 0;
			do {
				if ((dir & 1) == 1) {
					directionsTaken[takenIndex++] = DIR_DELTAS[j];
				} else {
					directionsRemaining[remainingIndex++] = DIR_DELTAS[j];
				}
				dir >>= 1;
			} while (++j < NUM_DIRS);
			DIRECTIONS_TAKEN[i] = directionsTaken;
			DIRECTIONS_REMAINING[i] = directionsRemaining;
		} while (++i < DIR_SIZE);
		// Pre-instantiate moves
		i = 0;
		do {
			MOVES[i] = Integer.valueOf(i);
		} while (++i < BOARD_SIZE);
	}

	// F = fence,  G = goal, W = wall, GP = goal post
	private static final int F_NW = DIR_5; // 5
	private static final int F_NE = DIR_7; // 7
	private static final int F_SW = DIR_3; // 3
	private static final int F_SE = DIR_1; //  1
	private static final int F_N = DIR_5 | DIR_6 | DIR_7; // 5, 6, 7
	private static final int F_S = DIR_1 | DIR_2 | DIR_3; // 1, 2, 3
	private static final int F_W = DIR_3 | DIR_4 | DIR_5; // 3, 4, 5
	private static final int F_E = DIR_1 | DIR_7 | DIR_8; // 1, 7, 8
	private static final int G_NW = DIR_4 | DIR_6 | DIR_7; // 4, 6, 7
	private static final int G_NE = DIR_5 | DIR_6 | DIR_8; // 5, 6, 8
	private static final int G_SW = DIR_1 | DIR_2 | DIR_4; // 1, 2, 4
	private static final int G_SE = DIR_2 | DIR_3 | DIR_8; // 2, 3, 8
	private static final int G_N = DIR_4 | DIR_8; // 4, 8
	private static final int G_S = G_N; // 4, 8
	private static final int W_NW = DIR_1 | DIR_2 | DIR_3 | DIR_4 | DIR_6 | DIR_7 | DIR_8; // 1, 2, 3, 4, 6, 7, 8
	private static final int W_NE = DIR_1 | DIR_2 | DIR_3 | DIR_4 | DIR_5 | DIR_6 | DIR_8; // 1, 2, 3, 4, 5, 6, 8
	private static final int W_SW = DIR_1 | DIR_2 | DIR_4 | DIR_5 | DIR_6 | DIR_7 | DIR_8; // 1, 2, 4, 5, 6, 7, 8
	private static final int W_SE = DIR_2 | DIR_3 | DIR_4 | DIR_5 | DIR_6 | DIR_7 | DIR_8; // 2, 3, 4, 5, 6, 7, 8
	private static final int W_N = DIR_1 | DIR_2 | DIR_3 | DIR_4 | DIR_8; // 1, 2, 3, 4, 8
	private static final int W_S = DIR_4 | DIR_5 | DIR_6 | DIR_7 | DIR_8; // 4, 5, 6, 7, 8
	private static final int W_W = DIR_1 | DIR_2 | DIR_6 | DIR_7 | DIR_8; // 1, 2, 6, 7, 8
	private static final int W_E = DIR_2 | DIR_3 | DIR_4 | DIR_5 | DIR_6; // 2, 3, 4, 5, 6
	private static final int GP_NW = DIR_1 | DIR_2 | DIR_8; // 1, 2, 8
	private static final int GP_NE = DIR_2 | DIR_3 | DIR_4; // 2, 3, 4
	private static final int GP_SW = DIR_6 | DIR_7 | DIR_8; // 6, 7, 8
	private static final int GP_SE = DIR_4 | DIR_5 | DIR_6; // 2, 3, 8

	public static final int CENTER = 84;

	public static int[] newInitialPosition() {
		return new int[] {
				0, F_NW, F_N, F_N, F_N, G_NW, G_N, G_NE, F_N, F_N, F_N, F_NE, 0,
				0, F_W, W_NW, W_N, W_N, GP_NW, 0, GP_NE, W_N, W_N, W_NE, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_W, 0, 0, 0, 0, 0, 0, 0, W_E, F_E, 0,
				0, F_W, W_SW, W_S, W_S, GP_SW, 0, GP_SE, W_S, W_S, W_SE, F_E, 0,
				0, F_SW, F_S, F_S, F_S, G_SW, G_S, G_SE, F_S, F_S, F_S, F_SE, 0
		};
	}

	public static void updateBoard(int[] board, int from, int to) {
		int dir = from - to + BOARD_WIDTH + 1;
		board[to] |= DELTA_TO_DIRS[dir];
		board[from] |= OPPOSITE_DELTA_TO_DIRS[dir];
	}

	public static void unupdateBoard(int[] board, int from, int to) {
		int dir = from - to + BOARD_WIDTH + 1;
		board[to] ^= DELTA_TO_DIRS[dir];
		board[from] ^= OPPOSITE_DELTA_TO_DIRS[dir];
	}

	public static Coordinate getCoordinate(int location) {
		return Coordinate.valueOf(location % BOARD_WIDTH, location / BOARD_WIDTH);
	}

	public static Integer getMove(int x, int y) {
		return Integer.valueOf(y * BOARD_WIDTH + x);
	}
}
