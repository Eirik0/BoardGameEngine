package game.forkjoinexample;

import game.IPosition;
import game.MoveList;

public class ForkJoinExampleTree implements IPosition<ForkJoinExampleNode, ForkJoinExampleTree> {
	private ForkJoinExampleNode currentNode;

	public ForkJoinExampleTree(int depth, int branchingFactor) {
		this(createTree(depth, branchingFactor, 0));
	}

	private ForkJoinExampleTree(ForkJoinExampleNode root) {
		currentNode = root;
	}

	public ForkJoinExampleNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public void getPossibleMoves(MoveList<ForkJoinExampleNode> moveList) {
		moveList.addAll(currentNode.getChildren());
	}

	@Override
	public int getCurrentPlayer() {
		return 1;
	}

	@Override
	public void makeMove(ForkJoinExampleNode move) {
		currentNode = move;
	}

	@Override
	public void unmakeMove(ForkJoinExampleNode move) {
		currentNode = move.getParent();
	}

	@Override
	public ForkJoinExampleTree createCopy() {
		return new ForkJoinExampleTree(currentNode);
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
