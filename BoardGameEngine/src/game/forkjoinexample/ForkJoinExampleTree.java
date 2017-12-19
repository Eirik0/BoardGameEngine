package game.forkjoinexample;

import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class ForkJoinExampleTree implements IPosition<ForkJoinExampleNode> {
	private ForkJoinExampleNode currentNode;
	private int currentPlayer;

	public ForkJoinExampleTree(int depth, int branchingFactor) {
		this(createTree(depth, branchingFactor, 0), TwoPlayers.PLAYER_1);
	}

	private ForkJoinExampleTree(ForkJoinExampleNode root, int currentPlayer) {
		currentNode = root;
		this.currentPlayer = currentPlayer;
	}

	public ForkJoinExampleNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public void getPossibleMoves(MoveList<ForkJoinExampleNode> moveList) {
		moveList.addAllQuietMoves(currentNode.getChildren(), this);
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void makeMove(ForkJoinExampleNode move) {
		currentNode = move;
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public void unmakeMove(ForkJoinExampleNode move) {
		currentNode = move.getParent();
		currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
	}

	@Override
	public ForkJoinExampleTree createCopy() {
		return new ForkJoinExampleTree(currentNode, currentPlayer);
	}

	@Override
	public String toString() {
		return currentNode.toString();
	}

	public static ForkJoinExampleNode createTree(int depth, int branchingFactor, int number) {
		if (depth < 1) {
			return null;
		} else if (depth == 1) {
			return new ForkJoinExampleNode(number, new ForkJoinExampleNode[] {});
		} else {
			ForkJoinExampleNode[] children = new ForkJoinExampleNode[branchingFactor];
			for (int i = 0; i < branchingFactor; ++i) {
				children[i] = createTree(depth - 1, branchingFactor, i);
			}
			return new ForkJoinExampleNode(number, children);
		}
	}
}
