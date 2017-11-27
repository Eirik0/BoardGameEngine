package game;

import java.util.Arrays;

public class ArrayMoveList<M> implements MoveList<M> {
	private final M[] moveArray;
	private int size = 0;

	@SuppressWarnings("unchecked")
	public ArrayMoveList(Integer capacity) {
		moveArray = (M[]) new Object[capacity];
	}

	@Override
	public <P extends IPosition<M, P>> void add(M move, P position) {
		moveArray[size++] = move;

	}

	@Override
	public <P extends IPosition<M, P>> void addAll(M[] moves, P position) {
		System.arraycopy(moves, 0, moveArray, size, moves.length);
		size += moves.length;

	}

	@Override
	public M get(int index) {
		return moveArray[index];
	}

	@Override
	public boolean contains(M move) {
		int i = 0;
		while (i < size) {
			if (moveArray[i].equals(move)) {
				return true;
			}
			++i;
		}
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public MoveList<M> subList(int beginIndex) {
		int newSize = size - beginIndex;
		ArrayMoveList<M> subList = new ArrayMoveList<>(newSize);
		System.arraycopy(moveArray, beginIndex, subList.moveArray, 0, newSize);
		subList.size = newSize;
		return subList;
	}

	@Override
	public String toString() {
		return Arrays.toString(moveArray);
	}
}
