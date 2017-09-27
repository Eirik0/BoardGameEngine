package game.chess;

import game.Coordinate;

public class ChessMove {
	final Coordinate from;
	final Coordinate to;

	public ChessMove(Coordinate from, Coordinate to, ChessPosition position) {
		this.from = from;
		this.to = to;
	}
}
