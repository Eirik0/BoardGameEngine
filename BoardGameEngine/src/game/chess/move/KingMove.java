package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class KingMove implements IChessMove {
	private final BasicChessMove basicMove;

	public KingMove(BasicChessMove basicMove) {
		this.basicMove = basicMove;
	}

	@Override
	public void applyMove(ChessPosition position, boolean changeState) {
		basicMove.applyMove(position, changeState);
		position.kingSquares[position.currentPlayer] = basicMove.to;
	}

	@Override
	public void unapplyMove(ChessPosition position, boolean changeState) {
		basicMove.unapplyMove(position, changeState);
		position.kingSquares[position.currentPlayer] = basicMove.from;
	}

	@Override
	public Coordinate getEnPassantSquare() {
		return basicMove.enPassantSquare;
	}

	@Override
	public int getPieceCaptured() {
		return basicMove.pieceCaptured;
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
		KingMove other = (KingMove) obj;
		return basicMove.equals(other.basicMove);
	}

	@Override
	public String toString() {
		return basicMove.toString();
	}
}
