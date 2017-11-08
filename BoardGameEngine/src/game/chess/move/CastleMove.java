package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class CastleMove implements IChessMove {
	private final BasicChessMove basicMove; // from king to rook
	private final Coordinate rookFrom;
	private final Coordinate rookTo;
	private final int castle;

	public CastleMove(BasicChessMove basicMove, int castle, Coordinate rookFrom, Coordinate rookTo) {
		this.basicMove = basicMove;
		this.castle = castle;
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
		if (changeState) {
			if ((castle & (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE)) != 0) {
				position.castleState = position.castleState & (BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE);
			} else {
				position.castleState = position.castleState & (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE);
			}
			position.enPassantSquare = null;
		}
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		int king = position.squares[basicMove.to.y][basicMove.to.x];
		int rook = position.squares[rookTo.y][rookTo.x];
		position.squares[basicMove.to.y][basicMove.to.x] = UNPLAYED;
		position.squares[rookTo.y][rookTo.x] = UNPLAYED;
		position.squares[basicMove.from.y][basicMove.from.x] = king;
		position.squares[rookFrom.y][rookFrom.x] = rook;
		position.kingSquares[position.currentPlayer] = basicMove.from;
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
		return ((castle & (BLACK_KING_CASTLE | WHITE_KING_CASTLE)) != 0) ? "O-O" : "O-O-O";
	}
}
