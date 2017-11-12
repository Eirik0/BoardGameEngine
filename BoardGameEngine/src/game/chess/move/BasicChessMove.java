package game.chess.move;

import game.Coordinate;
import game.chess.ChessFunctions;
import game.chess.ChessPosition;
import game.chess.fen.ForsythEdwardsNotation;

public class BasicChessMove implements IChessMove {
	public final Coordinate from;
	public final Coordinate to;
	final int pieceCaptured;
	final Coordinate enPassantSquare;

	public BasicChessMove(Coordinate from, Coordinate to, int pieceCaptured, Coordinate enPassantSquare) {
		this.from = from;
		this.to = to;
		this.pieceCaptured = pieceCaptured;
		this.enPassantSquare = enPassantSquare;
	}

	@Override
	public void applyMove(ChessPosition position, boolean changeState) {
		position.squares[to.y][to.x] = position.squares[from.y][from.x];
		position.squares[from.y][from.x] = UNPLAYED;
		if (changeState) {
			position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] - ChessFunctions.getPieceScore(pieceCaptured);
		}
	}

	@Override
	public void unapplyMove(ChessPosition position, boolean changeState) {
		position.squares[from.y][from.x] = position.squares[to.y][to.x];
		position.squares[to.y][to.x] = pieceCaptured;
		if (changeState) {
			position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] + ChessFunctions.getPieceScore(pieceCaptured);
		}
	}

	@Override
	public Coordinate getEnPassantSquare() {
		return enPassantSquare;
	}

	@Override
	public int getPieceCaptured() {
		return pieceCaptured;
	}

	@Override
	public Coordinate getFrom() {
		return from;
	}

	@Override
	public Coordinate getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * (prime + from.hashCode()) + to.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		BasicChessMove other = (BasicChessMove) obj;
		return from.equals(other.from) && to.equals(other.to);
	}

	@Override
	public String toString() {
		return ForsythEdwardsNotation.algebraicCoordinate(from) + (pieceCaptured == UNPLAYED ? "-" : "x") + ForsythEdwardsNotation.algebraicCoordinate(to);
	}
}
