package game;

import java.lang.reflect.InvocationTargetException;

public class MoveListFactory<M> {
	final int maxMoves;
	final Class<? extends MoveList<M>> analysisMoveListClass;

	@SuppressWarnings("unchecked")
	public MoveListFactory(int maxMoves) {
		this(maxMoves, (Class<? extends MoveList<M>>) ArrayMoveList.class);
	}

	public MoveListFactory(int maxMoves, Class<? extends MoveList<M>> analysisMoveListClass) {
		this.maxMoves = maxMoves;
		this.analysisMoveListClass = analysisMoveListClass;
	}

	public MoveList<M> newArrayMoveList() {
		return new ArrayMoveList<>(maxMoves);
	}

	public MoveList<M> newAnalysisMoveList() {
		try {
			return analysisMoveListClass.getDeclaredConstructor(Integer.TYPE).newInstance(Integer.valueOf(maxMoves));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
