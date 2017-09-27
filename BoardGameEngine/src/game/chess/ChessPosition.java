package game.chess;

import java.util.Collections;
import java.util.List;

import game.IPosition;

public class ChessPosition implements IPosition<ChessMove, ChessPosition>, ChessConstants {
	final int[][] cells;
	int currentPlayer;

	public ChessPosition() {
		cells = INITIAL_POSITION;
	}

	@Override
	public List<ChessMove> getPossibleMoves() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(ChessMove move) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unmakeMove(ChessMove move) {
		// TODO Auto-generated method stub

	}

	@Override
	public ChessPosition createCopy() {
		// TODO Auto-generated method stub
		return new ChessPosition();
	}
}
