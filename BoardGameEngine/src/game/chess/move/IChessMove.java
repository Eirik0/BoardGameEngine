package game.chess.move;

import game.Coordinate;
import game.chess.ChessConstants;
import game.chess.ChessPosition;

public interface IChessMove extends ChessConstants {
	public void applyMove(ChessPosition position);

	public void unapplyMove(ChessPosition position);

	public Coordinate getFrom();

	public Coordinate getTo();
}
