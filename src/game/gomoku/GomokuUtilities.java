package game.gomoku;

import game.Coordinate;

public class GomokuUtilities {
    static final int BOARD_WIDTH = 19;
    static final int BOARD_SIZE = (BOARD_WIDTH + 1) * (BOARD_WIDTH + 2) + 1;
    static final int START_BOARD_INDEX = BOARD_WIDTH + 2;
    static final int FINAL_BOARD_INDEX = BOARD_SIZE - BOARD_WIDTH - 2;

    static final Integer[] MOVES = new Integer[BOARD_SIZE];
    static final Coordinate[] MOVE_COORDS = new Coordinate[BOARD_SIZE];

    static final int NUM_DIRECTIONS = 8;
    static final int[] DIRECTIONS = { -19, -20, -21, -1, 1, 19, 20, 21 };

    static {
        int i = START_BOARD_INDEX;
        do {
            int j = 0;
            do {
                MOVES[i] = Integer.valueOf(i);
                MOVE_COORDS[i] = Coordinate.valueOf((i - BOARD_WIDTH - 1) / (BOARD_WIDTH + 1), i % 20 - 1);
                ++i;
            } while (++j < BOARD_WIDTH);
        } while (++i < FINAL_BOARD_INDEX);
    }

    static int[] newInitialPosition() {
        return new int[BOARD_SIZE];
    }

    static Integer getMove(int x, int y) {
        return MOVES[(y + 1) * (BOARD_WIDTH + 1) + x + 1];
    }

    static boolean winExists(int[] board, int moveInt, int player) {
        int i = 0;
        do {
            int dir = GomokuUtilities.DIRECTIONS[i];
            int inARow = 1;
            int pos = moveInt - dir;
            while (pos >= GomokuUtilities.START_BOARD_INDEX) {
                if (board[pos] == player) {
                    ++inARow;
                } else {
                    break;
                }
                pos -= dir;
            }
            pos = moveInt + dir;
            while (pos <= GomokuUtilities.FINAL_BOARD_INDEX) {
                if (board[pos] == player) {
                    ++inARow;
                } else {
                    break;
                }
                pos += dir;
            }
            if (inARow >= 5) {
                return true;
            }
        } while (++i < GomokuUtilities.NUM_DIRECTIONS);
        return false;
    }
}
