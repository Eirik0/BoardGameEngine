package game;

import java.util.Arrays;

public class ArrayMoveList<M> implements MoveList<M> {
	private final M[] dynamicMoveArray;
	private int dynamicArraySize = 0;

	private final M[] quietMoveArray;
	private int quietArraySize = 0;

	public ArrayMoveList(Integer capacity) {
		this(capacity, capacity);
	}

	@SuppressWarnings("unchecked")
	public ArrayMoveList(Integer dynamicCapacity, Integer quietCapacity) {
		dynamicMoveArray = (M[]) new Object[dynamicCapacity];
		quietMoveArray = (M[]) new Object[quietCapacity];
	}

	@Override
	public <P extends IPosition<M, P>> void setQuietMoves(M[] moves, P position) {
		System.arraycopy(moves, 0, quietMoveArray, 0, moves.length);
		quietArraySize = moves.length;

	}

	@Override
	public <P extends IPosition<M, P>> void addDynamicMove(M move, P position) {
		dynamicMoveArray[dynamicArraySize++] = move;
	}

	@Override
	public <P extends IPosition<M, P>> void addQuietMove(M move, P position) {
		quietMoveArray[quietArraySize++] = move;
	}

	@Override
	public M get(int index) {
		if (index < dynamicArraySize) {
			return dynamicMoveArray[index];
		} else {
			return quietMoveArray[index - dynamicArraySize];
		}
	}

	@Override
	public boolean contains(M move) {
		return arrayContains(move, dynamicMoveArray, dynamicArraySize) || arrayContains(move, quietMoveArray, quietArraySize);
	}

	private boolean arrayContains(M move, M[] array, int size) {
		int i = 0;
		while (i < size) {
			if (array[i].equals(move)) {
				return true;
			}
			++i;
		}
		return false;
	}

	@Override
	public int size() {
		return dynamicArraySize + quietArraySize;
	}

	@Override
	public int numDynamicMoves() {
		return dynamicArraySize;
	}

	@Override
	public void clear() {
		quietArraySize = 0;
		dynamicArraySize = 0;
	}

	@Override
	public MoveList<M> subList(int beginIndex) {
		int newDynamicArraySize = dynamicArraySize - beginIndex;
		ArrayMoveList<M> sublist;
		if (beginIndex < dynamicArraySize) {
			sublist = new ArrayMoveList<>(newDynamicArraySize, quietArraySize);
			System.arraycopy(dynamicMoveArray, beginIndex, sublist.dynamicMoveArray, 0, newDynamicArraySize);
			System.arraycopy(quietMoveArray, 0, sublist.quietMoveArray, 0, quietArraySize);
			sublist.dynamicArraySize = newDynamicArraySize;
			sublist.quietArraySize = quietArraySize;
		} else {
			int newQuietArraySize = newDynamicArraySize + quietArraySize;
			sublist = new ArrayMoveList<>(0, newQuietArraySize);
			System.arraycopy(quietMoveArray, beginIndex - dynamicArraySize, sublist.quietMoveArray, 0, newQuietArraySize);
			sublist.quietArraySize = newQuietArraySize;
		}
		return sublist;
	}

	@Override
	public String toString() {
		return "Dynamic: " + Arrays.toString(dynamicMoveArray) + ", Quiet" + Arrays.toString(quietMoveArray);
	}
}
