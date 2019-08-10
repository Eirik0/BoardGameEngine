package game;

public interface IPosition<M> {
    public void getPossibleMoves(MoveList<M> moveList);

    public int getCurrentPlayer();

    public void makeMove(M move);

    public void unmakeMove(M move);

    public IPosition<M> createCopy();
}
