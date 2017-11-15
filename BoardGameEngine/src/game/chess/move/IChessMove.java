package game.chess.move;

import game.chess.ChessConstants;
import game.chess.ChessPosition;

public interface IChessMove extends ChessConstants {
	public void applyMove(ChessPosition position, boolean changeState);

	public void unapplyMove(ChessPosition position, boolean changeState);

	public int getEnPassantSquare();

	public int getPieceCaptured();

	public int getFrom();

	public int getTo();
}
