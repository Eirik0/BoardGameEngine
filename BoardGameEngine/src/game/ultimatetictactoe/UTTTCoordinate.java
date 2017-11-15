package game.ultimatetictactoe;

public class UTTTCoordinate {
	public final int boardNum;
	public final int position;
	public final int currentBoard;

	public UTTTCoordinate(int boardNum, int position, int currentBoard) {
		this.boardNum = boardNum;
		this.position = position;
		this.currentBoard = currentBoard;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * (prime + boardNum) + position;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		UTTTCoordinate other = (UTTTCoordinate) obj;
		return boardNum == other.boardNum && position == other.position;
	}

	@Override
	public String toString() {
		return "(" + boardNum + ", " + position + ")";
	}
}
