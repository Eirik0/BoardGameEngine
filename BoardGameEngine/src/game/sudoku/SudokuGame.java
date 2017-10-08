package game.sudoku;

import game.IGame;

public class SudokuGame implements IGame<SudokuMove, SudokuPosition> {
	public static final String NAME = "Sudoku Generator";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return 1;
	}

	@Override
	public SudokuPosition newInitialPosition() {
		return new SudokuPosition();
	}
}
