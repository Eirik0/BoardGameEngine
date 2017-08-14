package game.ultimatetictactoe;

import game.Coordinate;

public class UTTTCoordinate {
	public final Coordinate coordinate;

	public final int currentBoard;

	public UTTTCoordinate(Coordinate coordinate, int currentBoard) {
		this.coordinate = coordinate;
		this.currentBoard = currentBoard;
	}

	@Override
	public int hashCode() {
		return coordinate.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		UTTTCoordinate other = (UTTTCoordinate) obj;
		return coordinate.equals(other.coordinate);
	}
}
