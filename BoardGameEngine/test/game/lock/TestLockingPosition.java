package game.lock;

import game.IPosition;
import game.MoveList;

public class TestLockingPosition implements IPosition<TestLockingNode, TestLockingPosition> {
	private TestLockingNode previousNode;
	private TestLockingNode currentNode;

	public TestLockingPosition(TestLockingNode initialNode) {
		currentNode = initialNode;
	}

	public TestLockingNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public void getPossibleMoves(MoveList<TestLockingNode> possibleMoves) {
		possibleMoves.addAll(currentNode.getPossibleMoves());
	}

	@Override
	public int getCurrentPlayer() {
		return 0;
	}

	@Override
	public void makeMove(TestLockingNode move) {
		previousNode = currentNode;
		currentNode = move;
	}

	@Override
	public void unmakeMove(TestLockingNode move) {
		currentNode = previousNode;
		previousNode = currentNode.getParent();
	}

	@Override
	public String toString() {
		return currentNode.toString();
	}

	@Override
	public TestLockingPosition createCopy() {
		TestLockingPosition copy = new TestLockingPosition(currentNode);
		copy.previousNode = previousNode;
		return copy;
	}
}
