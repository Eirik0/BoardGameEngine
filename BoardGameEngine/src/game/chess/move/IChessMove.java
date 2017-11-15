package game.chess.move;

import game.chess.ChessConstants;
import game.chess.ChessPosition;

public interface IChessMove extends ChessConstants {
	public void applyMove(ChessPosition position);

	public void unapplyMove(ChessPosition position);

	public void updateMaterial(ChessPosition position);

	public void unupdateMaterial(ChessPosition position);

	public int getEnPassantSquare();

	public int getPieceCaptured();

	public int getFrom();

	public int getTo();
}
