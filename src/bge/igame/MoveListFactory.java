package bge.igame;

import java.lang.reflect.InvocationTargetException;

public class MoveListFactory<M> {
    final int maxMoves;
    @SuppressWarnings("rawtypes")
    final Class<? extends MoveList> analysisMoveListClass;

    public MoveListFactory(int maxMoves) {
        this(maxMoves, ArrayMoveList.class);
    }

    public MoveListFactory(int maxMoves, @SuppressWarnings("rawtypes") Class<? extends MoveList> analysisMoveListClass) {
        this.maxMoves = maxMoves;
        this.analysisMoveListClass = analysisMoveListClass;
    }

    public MoveList<M> newArrayMoveList() {
        return new ArrayMoveList<>(maxMoves);
    }

    public MoveList<M> newAnalysisMoveList() {
        try {
            return analysisMoveListClass.getDeclaredConstructor(Integer.TYPE).newInstance(Integer.valueOf(maxMoves));
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public MoveListProvider<M> newAnalysisMoveListProvider() {
        return new MoveListProvider<>(this);
    }
}
