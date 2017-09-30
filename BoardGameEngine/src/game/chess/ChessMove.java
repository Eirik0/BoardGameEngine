package game.chess;

import game.Coordinate;

public class ChessMove {
	final Coordinate from;
	final Coordinate to;

	public ChessMove(Coordinate from, Coordinate to, ChessPosition position) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * (prime + from.hashCode()) + to.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		ChessMove other = (ChessMove) obj;
		return from.equals(other.from) && to.equals(other.to);
	}
}
