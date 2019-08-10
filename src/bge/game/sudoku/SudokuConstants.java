package bge.game.sudoku;

import bge.game.Coordinate;

public interface SudokuConstants {
    static final int NUM_DIGITS = 9;
    static final int TOTAL_CELLS = NUM_DIGITS * NUM_DIGITS;
    static final int MAX_MOVES = TOTAL_CELLS * NUM_DIGITS;

    static final int BOX = 0;
    static final int ROW = 1;
    static final int COLUMN = 2;

    static final int NO_DIGIT = 0;
    static final int DIGIT_1 = 1 << 0;
    static final int DIGIT_2 = 1 << 1;
    static final int DIGIT_3 = 1 << 2;
    static final int DIGIT_4 = 1 << 3;
    static final int DIGIT_5 = 1 << 4;
    static final int DIGIT_6 = 1 << 5;
    static final int DIGIT_7 = 1 << 6;
    static final int DIGIT_8 = 1 << 7;
    static final int DIGIT_9 = 1 << 8;
    static final int ALL_DIGITS = 0x1FF; // 1 1111 1111
    static final int[] DIGITS = new int[] { DIGIT_1, DIGIT_2, DIGIT_3, DIGIT_4, DIGIT_5, DIGIT_6, DIGIT_7, DIGIT_8, DIGIT_9 };

    static final int[][] POSSIBLE_DIGITS = createPossibleDigits();

    static int[][] createPossibleDigits() {
        int[][] possibleDigits = new int[ALL_DIGITS + 1][];
        int combination = 0;
        do {
            int size = 0;
            int[] tempDigits = new int[NUM_DIGITS];
            int i = 0;
            do {
                if ((combination & DIGITS[i]) != NO_DIGIT) {
                    tempDigits[size++] = DIGITS[i];
                }
            } while (++i < NUM_DIGITS);
            possibleDigits[combination] = new int[size];
            System.arraycopy(tempDigits, 0, possibleDigits[combination], 0, size);
        } while (++combination <= ALL_DIGITS);
        return possibleDigits;
    }

    //  0  1  2 |  3  4  5 |  6  7  8
    //  9 10 11 | 12 13 14 | 15 16 17
    // 18 19 20 | 21 22 23 | 24 25 26
    // ------------------------------
    // 27 28 29 | 30 31 32 | 33 34 35
    // 36 37 38 | 39 40 41 | 42 43 44
    // 45 46 47 | 48 49 50 | 51 52 53
    // ------------------------------
    // 54 55 56 | 57 58 59 | 60 61 62
    // 63 64 65 | 66 67 68 | 69 70 71
    // 72 73 74 | 75 76 77 | 78 79 80

    static SudokuCell[] newCells() {
        SudokuCell[] cells = new SudokuCell[TOTAL_CELLS];
        int i = 0;
        do {
            cells[i] = new SudokuCell();
        } while (++i < TOTAL_CELLS);
        return cells;
    }

    static int[] newUndecidedCells() {
        int[] undecided = new int[TOTAL_CELLS];
        int i = 0;
        do {
            undecided[i] = i;
        } while (++i < TOTAL_CELLS);
        return undecided;
    }

    static Coordinate getCoordinate(int location) {
        return Coordinate.valueOf(location / 9, location % 9);
    }

    static int mapDigit(int digit) {
        switch (digit) {
        case DIGIT_1:
            return 0;
        case DIGIT_2:
            return 1;
        case DIGIT_3:
            return 2;
        case DIGIT_4:
            return 3;
        case DIGIT_5:
            return 4;
        case DIGIT_6:
            return 5;
        case DIGIT_7:
            return 6;
        case DIGIT_8:
            return 7;
        case DIGIT_9:
            return 8;
        default:
            throw new IllegalStateException("Unexpected digit: " + digit);
        }
    }
}
