package game;

public interface IPosition<M, P extends IPosition<M, P>> {
	public void getPossibleMoves(MoveList<M> moveList);

	public int getCurrentPlayer();

	public void makeMove(M move);

	public void unmakeMove(M move);

	public P createCopy();
}
