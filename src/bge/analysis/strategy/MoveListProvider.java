package bge.analysis.strategy;

import bge.igame.MoveList;
import bge.igame.MoveListFactory;

public class MoveListProvider<M> {
    protected static final int MAX_DEPTH = 128;

    protected final MoveListFactory<M> moveListFactory;
    @SuppressWarnings("unchecked")
    private final MoveList<M>[] moveLists = new MoveList[MAX_DEPTH];

    public MoveListProvider(MoveListFactory<M> moveListFactory) {
        this.moveListFactory = moveListFactory;
    }

    public MoveList<M> getMoveList(int depth) {
        MoveList<M> moveList = moveLists[depth];
        if (moveList == null) {
            moveList = moveListFactory.newAnalysisMoveList();
            moveLists[depth] = moveList;
        }
        moveList.clear();
        return moveList;
    }

    public MoveListProvider<M> createCopy() {
        return new MoveListProvider<>(moveListFactory);
    }
}
