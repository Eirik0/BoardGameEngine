package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class CastleMove implements IChessMove {
	private final BasicChessMove basicMove; // from king to rook
	private final Coordinate kingCoordinate;
	private final Coordinate rookCoordinate;
	private final int castle;
	private final int currentCastleState;

	public CastleMove(BasicChessMove basicMove, int castle, int currentCastleState) {
		this.basicMove = basicMove;
		this.currentCastleState = currentCastleState;
		this.castle = castle;
		int rank = basicMove.from.y;
		if ((castle & (WHITE_KING_CASTLE | BLACK_KING_CASTLE)) != 0) {
			kingCoordinate = Coordinate.valueOf(basicMove.from.x - 2, rank);
			rookCoordinate = Coordinate.valueOf(kingCoordinate.x + 1, rank);
		} else {
			kingCoordinate = Coordinate.valueOf(basicMove.from.x + 2, rank);
			rookCoordinate = Coordinate.valueOf(kingCoordinate.x - 1, rank);
		}
	}

	@Override
	public void applyMove(ChessPosition position) {
		int king = position.squares[basicMove.from.y][basicMove.from.x];
		int rook = position.squares[basicMove.to.y][basicMove.to.x];
		position.squares[basicMove.from.y][basicMove.from.x] = UNPLAYED;
		position.squares[basicMove.to.y][basicMove.to.x] = UNPLAYED;
		position.squares[kingCoordinate.y][kingCoordinate.x] = king;
		position.squares[rookCoordinate.y][rookCoordinate.x] = rook;
		if ((castle & (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE)) != 0) {
			position.castleState = position.castleState & (BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE);
		} else {
			position.castleState = position.castleState & (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE);
		}
		position.enPassantSquare = null;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		int king = position.squares[kingCoordinate.y][kingCoordinate.x];
		int rook = position.squares[rookCoordinate.y][rookCoordinate.x];
		position.squares[kingCoordinate.y][kingCoordinate.x] = UNPLAYED;
		position.squares[rookCoordinate.y][rookCoordinate.x] = UNPLAYED;
		position.squares[basicMove.from.y][basicMove.from.x] = king;
		position.squares[basicMove.to.y][basicMove.to.x] = rook;
		position.castleState = currentCastleState;
		position.enPassantSquare = basicMove.currentEnPassantSquare;
	}

	@Override
	public Coordinate getFrom() {
		return basicMove.from;
	}

	@Override
	public Coordinate getTo() {
		return kingCoordinate;
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
