package bge.strategy.ts.forkjoin;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;

public interface ForkableTreeSearchFactory<M, P extends IPosition<M>> {
    IForkable<M> createNew(P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies);
}
