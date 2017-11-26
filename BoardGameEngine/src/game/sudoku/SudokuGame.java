package game.sudoku;

import game.IGame;

public class SudokuGame implements IGame<SudokuMove, SudokuPosition> {
	public static final String NAME = "Sudoku Generator";
	public static final int MAX_MOVES = SudokuPosition.BOARD_WIDTH * SudokuPosition.BOARD_WIDTH * SudokuPosition.BOARD_WIDTH;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return 1;
	}

	@Override
	public int getMaxMoves() {
		return MAX_MOVES;
	}

	@Override
	public SudokuPosition newInitialPosition() {
		return new SudokuPosition();
	}
}
