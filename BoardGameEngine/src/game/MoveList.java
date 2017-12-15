package game;

public interface MoveList<M> {
	public <P extends IPosition<M, P>> void addDynamicMove(M move, P position);

	public <P extends IPosition<M, P>> void addQuietMove(M move, P position);

	public <P extends IPosition<M, P>> void addAllQuietMoves(M[] moves, P position);

	public M get(int index);

	public boolean contains(M move);

	public int size();

	public int numDynamicMoves();

	public MoveList<M> subList(int beginIndex);

	public void clear();
}
