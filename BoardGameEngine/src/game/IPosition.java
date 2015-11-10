package game;

import java.util.List;

public interface IPosition<M, P extends IPosition<M, P>> {
	public List<M> getPossibleMoves();

	public int getCurrentPlayer();

	public void makeMove(M move);

	public void unmakeMove(M move);

	public P createCopy();
}
