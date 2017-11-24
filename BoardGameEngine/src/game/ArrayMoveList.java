package game;

import java.util.Arrays;

public class ArrayMoveList<M> implements MoveList<M> {
	private final M[] moveArray;
	private int size = 0;

	@SuppressWarnings("unchecked")
	public ArrayMoveList(int capacity) {
		moveArray = (M[]) new Object[capacity];
	}

	@Override
	public void add(M move) {
		moveArray[size++] = move;

	}

	@Override
	public void addAll(M[] moves) {
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
	public String toString() {
		return Arrays.toString(moveArray);
	}
}
