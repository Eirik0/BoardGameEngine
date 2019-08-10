package bge.game.forkjoinexample;

import bge.game.ArrayMoveList;
import bge.game.IPosition;
import bge.game.MoveList;

public class ForkJoinMoveList implements MoveList<ForkJoinExampleNode> {
    private final ArrayMoveList<ForkJoinExampleNode> arrayMoveList;

    public ForkJoinMoveList(int capacity) {
        arrayMoveList = new ArrayMoveList<>(capacity);
    }

    @Override
    public void addDynamicMove(ForkJoinExampleNode move, IPosition<ForkJoinExampleNode> position) {
        ForkJoinExampleThreadTracker.branchVisited(move.getParent(), move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
        arrayMoveList.addDynamicMove(move, position);
    }

    @Override
    public void addAllDynamicMoves(ForkJoinExampleNode[] moves, IPosition<ForkJoinExampleNode> position) {
        int i = 0;
        while (i < moves.length) {
            addDynamicMove(moves[i], position);
            ++i;
        }
    }

    @Override
    public void addQuietMove(ForkJoinExampleNode move, IPosition<ForkJoinExampleNode> position) {
        ForkJoinExampleThreadTracker.branchVisited(move.getParent(), move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
        arrayMoveList.addQuietMove(move, position);
    }

    @Override
    public void addAllQuietMoves(ForkJoinExampleNode[] moves, IPosition<ForkJoinExampleNode> position) {
        int i = 0;
        while (i < moves.length) {
            addQuietMove(moves[i], position);
            ++i;
        }
    }

    @Override
    public ForkJoinExampleNode get(int index) {
        return arrayMoveList.get(index);
    }

    @Override
    public boolean contains(ForkJoinExampleNode move) {
        return arrayMoveList.contains(move);
    }

    @Override
    public int size() {
        return arrayMoveList.size();
    }

    @Override
    public int numDynamicMoves() {
        return arrayMoveList.numDynamicMoves();
    }

    @Override
    public MoveList<ForkJoinExampleNode> subList(int beginIndex) {
        return arrayMoveList.subList(beginIndex);
    }

    @Override
    public void clear() {
        arrayMoveList.clear();
    }
}
