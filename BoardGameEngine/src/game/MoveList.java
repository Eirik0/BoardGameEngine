package game;

public interface MoveList<M> {
	public static final int MAX_SIZE = 512;

	public void add(M move);

	public void addAll(M[] moves);

	public M get(int index);

	public boolean contains(M move);

	public int size();
}
