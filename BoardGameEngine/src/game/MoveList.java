package game;

public interface MoveList<M> {
	public void add(M move);

	public void addAll(M[] moves);

	public M get(int index);

	public boolean contains(M move);

	public int size();

	public MoveList<M> subList(int beginIndex);

	public void clear();
}
