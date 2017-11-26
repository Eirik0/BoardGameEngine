package game;

public class MoveListFactory<M> {
	final int maxMoves;

	public MoveListFactory(int maxMoves) {
		this.maxMoves = maxMoves;
	}

	public MoveList<M> newArrayMoveList() {
		return new ArrayMoveList<>(maxMoves);
	}
}
