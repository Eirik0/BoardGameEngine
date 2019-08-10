package game.sudoku;

public class SudokuCell implements SudokuConstants {
	private static final int BOXES_PER_CELL = 3;

	final SudokuBox[] boxes;
	int digit;

	public SudokuCell() {
		this(NO_DIGIT);
	}

	private SudokuCell(int digit) {
		boxes = new SudokuBox[BOXES_PER_CELL];
		this.digit = digit;
	}

	public void setBox(SudokuBox box, int boxRowColumn) {
		boxes[boxRowColumn] = box;
	}

	public void setDigit(int digit) {
		this.digit = digit;
		boxes[0].digitAdded(digit);
		boxes[1].digitAdded(digit);
		boxes[2].digitAdded(digit);
	}

	public void unsetDigit() {
		boxes[0].digitRemoved(digit);
		boxes[1].digitRemoved(digit);
		boxes[2].digitRemoved(digit);
		digit = NO_DIGIT;
	}

	public int[] getPossibleDigits() {
		return POSSIBLE_DIGITS[boxes[0].possibleDigits & boxes[1].possibleDigits & boxes[2].possibleDigits];
	}

	public SudokuCell createCopy() {
		return new SudokuCell(digit);
	}

	@Override
	public String toString() {
		return Integer.toString(SudokuConstants.mapDigit(digit) + 1);
	}
}
