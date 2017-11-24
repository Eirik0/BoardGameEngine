package game.value;

import java.util.Arrays;

public class TestGameNode {
	private final int value;

	private TestGameNode parent = null;
	private TestGameNode[] moves = {};

	public TestGameNode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public TestGameNode[] getPossibleMoves() {
		return moves;
	}

	public TestGameNode setMoves(TestGameNode... branches) {
		for (TestGameNode child : branches) {
			child.parent = this;
		}
		moves = branches;
		return this;
	}

	public TestGameNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		int[] movesArr = new int[moves.length];
		for (int i = 0; i < movesArr.length; i++) {
			movesArr[i] = moves[i].value;
		}
		return value + " -> " + Arrays.toString(movesArr);
	}
}
