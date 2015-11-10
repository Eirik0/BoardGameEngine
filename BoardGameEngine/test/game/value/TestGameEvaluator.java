package game.value;

import analysis.IPositionEvaluator;

public class TestGameEvaluator implements IPositionEvaluator<TestGameNode, TestGamePosition> {
	@Override
	public double evaluate(TestGamePosition position, int player) {
		return position.getCurrentNode().getValue();
	}
}
