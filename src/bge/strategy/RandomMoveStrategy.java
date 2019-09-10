package bge.strategy;

import java.util.Random;

import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;

public class RandomMoveStrategy<M> implements IStrategy<M> {
    private static final Random RANDOM = new Random();

    private final MoveListFactory<M> moveListFactory;

    public RandomMoveStrategy(MoveListFactory<M> moveListFactory) {
        this.moveListFactory = moveListFactory;
    }

    @Override
    public M getMove(IPosition<M> position) {
        MoveList<M> moveList = moveListFactory.newArrayMoveList();
        position.getPossibleMoves(moveList);
        return moveList.get(RANDOM.nextInt(moveList.size()));
    }
}
