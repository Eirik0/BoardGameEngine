package game.sudoku;

public class SudokuBox implements SudokuConstants {
	final SudokuCell[] undeterminedCells;
	int numUndetermined;
	final SudokuCell[] determinedCells;
	int numDetermined;

	int possibleDigits;

	public static void initBox(SudokuCell[] cells, int boxRowColumn) {
		SudokuBox box = new SudokuBox();
		int i = 0;
		do {
			SudokuCell cell = cells[i];
			if (cell.digit == NO_DIGIT) {
				box.undeterminedCells[box.numUndetermined++] = cell;
			} else {
				box.determinedCells[box.numDetermined++] = cell;
				box.possibleDigits &= ~cell.digit;
			}
			cell.setBox(box, boxRowColumn);
		} while (++i < NUM_DIGITS);
	}

	public SudokuBox() {
		undeterminedCells = new SudokuCell[NUM_DIGITS];
		numUndetermined = 0;
		determinedCells = new SudokuCell[NUM_DIGITS];
		numDetermined = 0;
		possibleDigits = ALL_DIGITS;
	}

	public void digitAdded(int digit) {
		int i = 0;
		do {
			if (undeterminedCells[i].digit == digit) {
				determinedCells[numDetermined++] = undeterminedCells[i];
				undeterminedCells[i] = undeterminedCells[--numUndetermined];
				break;
			}
		} while (++i < numUndetermined);
		possibleDigits &= ~digit;
	}

	public void digitRemoved(int digit) {
		// Assume we are unadding the digit we just added
		undeterminedCells[numUndetermined++] = determinedCells[--numDetermined];
		possibleDigits |= digit;
	}
}
