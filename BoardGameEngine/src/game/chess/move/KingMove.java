package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class KingMove implements IChessMove {
	private final BasicChessMove basicMove;
	private final int currentCastleState;;
	private final boolean white;

	public KingMove(BasicChessMove basicMove, int currentCastleState, boolean white) {
		this.basicMove = basicMove;
		this.currentCastleState = currentCastleState;
		this.white = white;
	}

	@Override
	public void applyMove(ChessPosition position) {
		basicMove.applyMove(position);
		if (white) {
			position.castleState &= (BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE);
			position.whiteKingSquare = basicMove.to;
		} else {
			position.castleState &= (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE);
			position.blackKingSquare = basicMove.to;
		}
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		basicMove.applyMove(position);
		if (white) {
			position.whiteKingSquare = basicMove.from;
		} else {
			position.blackKingSquare = basicMove.from;
		}
		position.castleState = currentCastleState;
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
