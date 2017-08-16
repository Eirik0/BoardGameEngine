package game.forkjoinexample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import game.IPosition;

public class ForkJoinExampleTree implements IPosition<ForkJoinExampleNode, ForkJoinExampleTree> {
	private ForkJoinExampleNode currentNode;

	public ForkJoinExampleTree(int depth, int branchingFactor) {
		this(createTree(depth, branchingFactor));
	}

	private ForkJoinExampleTree(ForkJoinExampleNode root) {
		currentNode = root;
	}

	public ForkJoinExampleNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public List<ForkJoinExampleNode> getPossibleMoves() {
		return currentNode.getChildren();
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

	public static ForkJoinExampleNode createTree(int depth, int branchingFactor) {
		if (depth < 1) {
			return null;
		} else if (depth == 1) {
			return new ForkJoinExampleNode(Collections.emptyList());
		} else {
			List<ForkJoinExampleNode> children = new ArrayList<>();
			for (int i = 0; i < branchingFactor; ++i) {
				children.add(createTree(depth - 1, branchingFactor));
			}
			return new ForkJoinExampleNode(children);
		}
	}
}
