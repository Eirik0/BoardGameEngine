package game;

public interface MoveList<M> {
	public <P extends IPosition<M, P>> void add(M move, P position);

	public <P extends IPosition<M, P>> void addAll(M[] moves, P position);

	public M get(int index);

	public boolean contains(M move);

	public int size();

	public MoveList<M> subList(int beginIndex);

	public void clear();
}
