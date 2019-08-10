package bge.game;

public interface MoveList<M> {
    public void addDynamicMove(M move, IPosition<M> position);

    public void addAllDynamicMoves(M[] moves, IPosition<M> position);

    public void addQuietMove(M move, IPosition<M> position);

    public void addAllQuietMoves(M[] moves, IPosition<M> position);

    public M get(int index);

    public boolean contains(M move);

    public int size();

    public int numDynamicMoves();

    public MoveList<M> subList(int beginIndex);

    public void clear();
}
