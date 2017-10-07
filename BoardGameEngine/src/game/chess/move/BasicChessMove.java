package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class BasicChessMove implements IChessMove {
	final Coordinate from;
	final Coordinate to;
	final int pieceCaptured;
	final Coordinate currentEnPassantSquare;

	public BasicChessMove(Coordinate from, Coordinate to, int pieceCaptured, Coordinate currentEnPassantSquare) {
		this.from = from;
		this.to = to;
		this.pieceCaptured = pieceCaptured;
		this.currentEnPassantSquare = currentEnPassantSquare;
	}

	@Override
	public void applyMove(ChessPosition position) {
		position.squares[to.y][to.x] = position.squares[from.y][from.x];
		position.squares[from.y][from.x] = UNPLAYED;
		position.enPassantSquare = null;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		position.squares[from.y][from.x] = position.squares[to.y][to.x];
		position.squares[to.y][to.x] = pieceCaptured;
		position.enPassantSquare = currentEnPassantSquare;
	}

	@Override
	public Coordinate getFrom() {
		return from;
	}

	@Override
	public Coordinate getTo() {
		return to;
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
		BasicChessMove other = (BasicChessMove) obj;
		return from.equals(other.from) && to.equals(other.to);
	}
}
