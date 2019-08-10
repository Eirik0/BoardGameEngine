package bge.game.sudoku;

public class SudokuMove implements SudokuConstants {
    public final int location;
    public final int digit;

    private static final SudokuMove[] sudokuMoves = new SudokuMove[MAX_MOVES];

    public static SudokuMove valueOf(int location, int digit) {
        int index = location * NUM_DIGITS + SudokuConstants.mapDigit(digit);
        SudokuMove move = sudokuMoves[index];
        if (move == null) {
            move = new SudokuMove(location, digit);
            sudokuMoves[index] = move;
        }
        return move;
    }

    private SudokuMove(int location, int digit) {
        this.location = location;
        this.digit = digit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + location) + digit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SudokuMove other = (SudokuMove) obj;
        return location == other.location && digit == other.digit;
    }

    @Override
    public String toString() {
        return SudokuConstants.getCoordinate(location).toString() + ": " + Integer.toString(SudokuConstants.mapDigit(digit) + 1);
    }
}
