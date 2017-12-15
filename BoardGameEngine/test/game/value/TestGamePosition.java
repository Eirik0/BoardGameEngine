package game.value;

import game.IPosition;
import game.MoveList;

public class TestGamePosition implements IPosition<TestGameNode, TestGamePosition> {
	private TestGameNode previousNode;
	private TestGameNode currentNode;

	private int currentPlayer = 0;

	public TestGamePosition(TestGameNode initialNode) {
		currentNode = initialNode;
	}

	public TestGamePosition(TestGameNode currentNode, TestGameNode previousNode, int currentPlayer) {
		this.previousNode = previousNode;
		this.currentNode = currentNode;
		this.currentPlayer = currentPlayer;
	}

	public TestGameNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public void getPossibleMoves(MoveList<TestGameNode> possibleMoves) {
		possibleMoves.addAllQuietMoves(currentNode.getPossibleMoves(), this);
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(TestGameNode move) {
		previousNode = currentNode;
		currentNode = move;
		swapPlayers();
	}

	@Override
	public void unmakeMove(TestGameNode move) {
		currentNode = previousNode;
		previousNode = currentNode.getParent();
		swapPlayers();
	}

	private void swapPlayers() {
		currentPlayer = currentPlayer ^ 1;
	}

	@Override
	public String toString() {
		return currentNode.toString();
	}

	@Override
	public TestGamePosition createCopy() {
		return new TestGamePosition(currentNode, previousNode, currentPlayer);
	}

	/**
	 * 1: 2<br>
	 * 2: 1 -> 5<br>
	 * 3: 2 -> 4 -> 12<br>
	 * 4: 1 -> 5 -> 10 -> 25<br>
	 *
	 * @return
	 */
	public static TestGamePosition createTestPosition() {
		return new TestGamePosition(
				new TestGameNode(0).setMoves(
						new TestGameNode(-1).setMoves(
								new TestGameNode(6).setMoves(
										new TestGameNode(-7).setMoves(
												new TestGameNode(30),
												new TestGameNode(29)),
										new TestGameNode(-8).setMoves(
												new TestGameNode(28),
												new TestGameNode(27))),
								new TestGameNode(5).setMoves(
										new TestGameNode(-9).setMoves(
												new TestGameNode(26),
												new TestGameNode(25)),
										new TestGameNode(-10).setMoves(
												new TestGameNode(24),
												new TestGameNode(23)))),
						new TestGameNode(-2).setMoves(
								new TestGameNode(4).setMoves(
										new TestGameNode(-11).setMoves(
												new TestGameNode(22),
												new TestGameNode(21)),
										new TestGameNode(-12).setMoves(
												new TestGameNode(20),
												new TestGameNode(19))),
								new TestGameNode(3).setMoves(
										new TestGameNode(-13).setMoves(
												new TestGameNode(18),
												new TestGameNode(17)),
										new TestGameNode(-14).setMoves(
												new TestGameNode(16),
												new TestGameNode(15))))));
	}
}
