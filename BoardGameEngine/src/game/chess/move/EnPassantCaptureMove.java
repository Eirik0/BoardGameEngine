package game.chess.move;

import game.Coordinate;
import game.chess.ChessFunctions;
import game.chess.ChessPosition;

public class EnPassantCaptureMove implements IChessMove {
	private final BasicChessMove basicMove;
	public final int pawnDirection;

	public EnPassantCaptureMove(BasicChessMove basicMove, int pawnDirection) {
		this.basicMove = basicMove;
		this.pawnDirection = pawnDirection;
	}

	@Override
	public void applyMove(ChessPosition position, boolean changeState) {
		basicMove.applyMove(position, changeState);
		position.squares[basicMove.to.y - pawnDirection][basicMove.to.x] = UNPLAYED;
	}

	@Override
	public void unapplyMove(ChessPosition position, boolean changeState) {
		position.squares[basicMove.to.y - pawnDirection][basicMove.to.x] = basicMove.pieceCaptured;
		position.squares[basicMove.from.y][basicMove.from.x] = position.squares[basicMove.to.y][basicMove.to.x];
		position.squares[basicMove.to.y][basicMove.to.x] = UNPLAYED;
		if (changeState) {
			position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] + ChessFunctions.getPieceScore(basicMove.pieceCaptured);
		}
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
		EnPassantCaptureMove other = (EnPassantCaptureMove) obj;
		return basicMove.equals(other.basicMove);
	}

	@Override
	public String toString() {
		return basicMove.toString();
	}
}
