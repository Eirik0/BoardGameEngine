package game.value;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestGameNode {
	private final int value;

	private TestGameNode parent = null;
	private List<TestGameNode> moves = Collections.emptyList();

	public TestGameNode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public List<TestGameNode> getPossibleMoves() {
		return moves;
	}

	public TestGameNode setMoves(TestGameNode... branches) {
		for (TestGameNode child : branches) {
			child.parent = this;
		}
		moves = Arrays.asList(branches);
		return this;
	}

	public TestGameNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		int[] movesArr = new int[moves.size()];
		for (int i = 0; i < movesArr.length; i++) {
			movesArr[i] = moves.get(i).value;
		}
		return value + " -> " + Arrays.toString(movesArr);
	}
}
