package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class CastleMove implements IChessMove {
	private final BasicChessMove basicMove; // from king to rook
	private final Coordinate rookFrom;
	private final Coordinate rookTo;

	public CastleMove(BasicChessMove basicMove, Coordinate rookFrom, Coordinate rookTo) {
		this.basicMove = basicMove;
		this.rookFrom = rookFrom;
		this.rookTo = rookTo;
	}

	@Override
	public void applyMove(ChessPosition position, boolean changeState) {
		int king = position.squares[basicMove.from.y][basicMove.from.x];
		int rook = position.squares[rookFrom.y][rookFrom.x];
		position.squares[basicMove.from.y][basicMove.from.x] = UNPLAYED;
		position.squares[rookFrom.y][rookFrom.x] = UNPLAYED;
		position.squares[basicMove.to.y][basicMove.to.x] = king;
		position.squares[rookTo.y][rookTo.x] = rook;
		position.kingSquares[position.currentPlayer] = basicMove.to;
	}

	@Override
	public void unapplyMove(ChessPosition position, boolean changeState) {
		int king = position.squares[basicMove.to.y][basicMove.to.x];
		int rook = position.squares[rookTo.y][rookTo.x];
		position.squares[basicMove.to.y][basicMove.to.x] = UNPLAYED;
		position.squares[rookTo.y][rookTo.x] = UNPLAYED;
		position.squares[basicMove.from.y][basicMove.from.x] = king;
		position.squares[rookFrom.y][rookFrom.x] = rook;
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
		CastleMove other = (CastleMove) obj;
		return basicMove.equals(other.basicMove);
	}

	@Override
	public String toString() {
		return rookFrom.x == H_FILE ? "O-O" : "O-O-O";
	}
}
