package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class EnPassantMove implements IChessMove {
	private final BasicChessMove basicMove;
	private final Coordinate enPassantSquare;

	public EnPassantMove(BasicChessMove basicMove, Coordinate enPassantSquare) {
		this.basicMove = basicMove;
		this.enPassantSquare = enPassantSquare;
	}

	@Override
	public void applyMove(ChessPosition position) {
		basicMove.applyMove(position);
		position.enPassantSquare = enPassantSquare;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		basicMove.unapplyMove(position);
	}

	@Override
	public Coordinate getFrom() {
		return basicMove.from;
	}

	@Override
	public Coordinate getTo() {
		return basicMove.to;
	}

	@Override
	public int hashCode() {
		return basicMove.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		EnPassantMove other = (EnPassantMove) obj;
		return basicMove.equals(other.basicMove);
	}
}
