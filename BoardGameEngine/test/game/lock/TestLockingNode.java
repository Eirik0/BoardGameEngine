package game.lock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestLockingNode {
	private volatile boolean isLocked;

	private TestLockingNode parent = null;
	private List<TestLockingNode> moves = Collections.emptyList();

	public TestLockingNode(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public synchronized void unlock() {
		isLocked = false;
		notify();
	}

	public synchronized void waitForLock() {
		while (isLocked) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public List<TestLockingNode> getPossibleMoves() {
		return moves;
	}

	public TestLockingNode setMoves(TestLockingNode... branches) {
		for (TestLockingNode child : branches) {
			child.parent = this;
		}
		moves = Arrays.asList(branches);
		return this;
	}

	public TestLockingNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		boolean[] movesArr = new boolean[moves.size()];
		for (int i = 0; i < movesArr.length; i++) {
			movesArr[i] = moves.get(i).isLocked;
		}
		return isLocked + " -> " + Arrays.toString(movesArr);
	}
}
