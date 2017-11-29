package game.lock;

import analysis.IPositionEvaluator;
import game.MoveList;

public class TestLockingEvaluator implements IPositionEvaluator<TestLockingNode, TestLockingPosition> {
	@Override
	public double evaluate(TestLockingPosition position, MoveList<TestLockingNode> possibleMoves, int player) {
		position.getCurrentNode().waitForLock();
		return 0;
	}
}
