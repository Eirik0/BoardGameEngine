package game.chess.move;

import game.chess.ChessFunctions;
import game.chess.ChessPosition;
import game.chess.fen.ForsythEdwardsNotation;

public class BasicChessMove implements IChessMove {
	public final int from;
	public final int to;
	final int pieceCaptured;
	final int enPassantSquare;

	public BasicChessMove(int from, int to, int pieceCaptured, int enPassantSquare) {
		this.from = from;
		this.to = to;
		this.pieceCaptured = pieceCaptured;
		this.enPassantSquare = enPassantSquare;
	}

	@Override
	public void applyMove(ChessPosition position) {
		position.squares[to] = position.squares[from];
		position.squares[from] = UNPLAYED;
	}

	@Override
	public void unapplyMove(ChessPosition position) {
		position.squares[from] = position.squares[to];
		position.squares[to] = pieceCaptured;
	}

	@Override
	public void updateMaterial(ChessPosition position) {
		ChessFunctions.updatePiece(position, from, to, position.squares[from], position.currentPlayer);
		if (pieceCaptured != 0) {
			position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] - ChessFunctions.getPieceScore(pieceCaptured);
			ChessFunctions.removePiece(position, to, pieceCaptured, position.otherPlayer);
		}
	}

	@Override
	public void unupdateMaterial(ChessPosition position) {
		ChessFunctions.updatePiece(position, to, from, position.squares[from], position.currentPlayer);
		if (pieceCaptured != 0) {
			position.materialScore[position.otherPlayer] = position.materialScore[position.otherPlayer] + ChessFunctions.getPieceScore(pieceCaptured);
			ChessFunctions.addPiece(position, to, pieceCaptured, position.otherPlayer);
		}
	}

	@Override
	public int getEnPassantSquare() {
		return enPassantSquare;
	}

	@Override
	public int getPieceCaptured() {
		return pieceCaptured;
	}

	@Override
	public int getFrom() {
		return from;
	}

	@Override
	public int getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * (prime + from) + to;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		BasicChessMove other = (BasicChessMove) obj;
		return from == other.from && to == other.to;
	}

	@Override
	public String toString() {
		return ForsythEdwardsNotation.algebraicCoordinate(from) + (pieceCaptured == UNPLAYED ? "-" : "x") + ForsythEdwardsNotation.algebraicCoordinate(to);
	}
}
