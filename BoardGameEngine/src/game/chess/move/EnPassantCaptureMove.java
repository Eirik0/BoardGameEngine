package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class EnPassantCaptureMove implements IChessMove {
	private final BasicChessMove basicMove;

	public EnPassantCaptureMove(BasicChessMove basicMove) {
		this.basicMove = basicMove;
	}

	@Override
	public void applyMove(ChessPosition position) {
		position.squares[basicMove.currentEnPassantSquare.y][basicMove.currentEnPassantSquare.x] = position.squares[basicMove.from.y][basicMove.from.x];
		position.squares[basicMove.from.y][basicMove.from.x] = UNPLAYED;
		position.enPassantSquare = null;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		position.squares[basicMove.from.y][basicMove.from.x] = position.squares[basicMove.currentEnPassantSquare.y][basicMove.currentEnPassantSquare.x];
		position.squares[basicMove.currentEnPassantSquare.y][basicMove.currentEnPassantSquare.x] = basicMove.pieceCaptured;
		position.enPassantSquare = basicMove.currentEnPassantSquare;
	}

	@Override
	public Coordinate getFrom() {
		return basicMove.from;
	}

	@Override
	public Coordinate getTo() {
		return basicMove.currentEnPassantSquare;
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
		EnPassantCaptureMove other = (EnPassantCaptureMove) obj;
		return basicMove.equals(other.basicMove);
	}
}
