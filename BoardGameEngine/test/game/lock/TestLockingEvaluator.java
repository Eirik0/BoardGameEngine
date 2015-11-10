package game.lock;

import analysis.IPositionEvaluator;

public class TestLockingEvaluator implements IPositionEvaluator<TestLockingNode, TestLockingPosition> {
	@Override
	public double evaluate(TestLockingPosition position, int player) {
		position.getCurrentNode().waitForLock();
		return 0;
	}
}
