package game.chess.move;

import game.Coordinate;
import game.chess.ChessPosition;

public class CastleBreakingMove implements IChessMove {
	private final BasicChessMove basicMove;
	int castlesBroken;

	public CastleBreakingMove(BasicChessMove basicMove, int castlesBroken) {
		this.basicMove = basicMove;
		this.castlesBroken = castlesBroken;
	}

	@Override
	public void applyMove(ChessPosition position) {
		basicMove.applyMove(position);
		int castlesBrokenReverse = (castlesBroken ^ INITIAL_CASTLE_STATE) & INITIAL_CASTLE_STATE;
		position.castleState &= castlesBrokenReverse;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		basicMove.applyMove(position);
		position.castleState = position.castleState | castlesBroken;
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
		CastleBreakingMove other = (CastleBreakingMove) obj;
		return basicMove.equals(other.basicMove);
	}
}
