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
		if (changeState) {
			if (position.white) { // mask out the opposite player
				position.castleState &= (BLACK_KING_CASTLE | BLACK_QUEEN_CASTLE);
			} else {
				position.castleState &= (WHITE_KING_CASTLE | WHITE_QUEEN_CASTLE);
			}
		}
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		basicMove.unapplyMove(position);
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
		KingMove other = (KingMove) obj;
		return basicMove.equals(other.basicMove);
	}

	@Override
	public String toString() {
		return basicMove.toString();
	}
}
