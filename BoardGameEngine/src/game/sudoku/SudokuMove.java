package game.sudoku;

import game.Coordinate;

public class SudokuMove {
	public final Coordinate coordinate;
	public final int number;

	public SudokuMove(Coordinate coordinate, int number) {
		this.coordinate = coordinate;
		this.number = number;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coordinate == null) ? 0 : coordinate.hashCode());
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		SudokuMove other = (SudokuMove) obj;
		return coordinate.equals(other.coordinate) && number == other.number;
	}
}