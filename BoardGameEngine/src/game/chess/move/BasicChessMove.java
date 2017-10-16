package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class BasicChessMove implements IChessMove {
	public final Coordinate from;
	public final Coordinate to;
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

	@Override
	public String toString() {
		return algebraicCoordinate(from) + (pieceCaptured == UNPLAYED ? "-" : "x") + algebraicCoordinate(to);
	}

	private String algebraicCoordinate(Coordinate coordinate) {
		return getFile(coordinate.x) + (coordinate.y + 1);
	}

	private String getFile(int x) {
		switch (x) {
		case H_FILE:
			return "h";
		case G_FILE:
			return "g";
		case F_FILE:
			return "f";
		case E_FILE:
			return "e";
		case D_FILE:
			return "d";
		case C_FILE:
			return "c";
		case B_FILE:
			return "b";
		case A_FILE:
			return "a";
		default:
			throw new UnsupportedOperationException("Unknown file " + x);
		}
	}
}
