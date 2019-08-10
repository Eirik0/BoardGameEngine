package bge.game.value;

import bge.analysis.IPositionEvaluator;
import bge.game.MoveList;

public class TestGameEvaluator implements IPositionEvaluator<TestGameNode, TestGamePosition> {
    @Override
    public double evaluate(TestGamePosition position, MoveList<TestGameNode> possibleMoves) {
        return position.getCurrentNode().getValue();
    }
}
