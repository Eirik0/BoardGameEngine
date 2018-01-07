package game.forkjoinexample;

import java.util.Random;

import analysis.AnalysisResult;
import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class ForkJoinExampleTree implements IPosition<ForkJoinExampleNode> {
	public static final int DEPTH = 15;
	public static final int BRANCHING_FACTOR = 2;

	private static final Random RANDOM = new Random();

	private ForkJoinExampleNode currentNode;

	public ForkJoinExampleTree(int depth, int branchingFactor) {
		this(createTreeWithScores(depth, branchingFactor, 0));
	}

	private ForkJoinExampleTree(ForkJoinExampleNode root) {
		currentNode = root;
	}

	public ForkJoinExampleNode getCurrentNode() {
		return currentNode;
	}

	@Override
	public void getPossibleMoves(MoveList<ForkJoinExampleNode> moveList) {
		for (ForkJoinExampleNode child : currentNode.getChildren()) {
			if (child.isQuiescent()) {
				moveList.addDynamicMove(child, this);
			} else {
				moveList.addQuietMove(child, this);
			}
		}
	}

	@Override
	public int getCurrentPlayer() {
		return currentNode.getPlayer();
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

	@Override
	public String toString() {
		return currentNode.toString();
	}

	private static ForkJoinExampleNode createTreeWithScores(int depth, int branchingFactor, int number) {
		ForkJoinExampleNode node = createTree(depth, branchingFactor, true, number);
		setScores(node, node.getChildren(), 1);
		return node;
	}

	private static void setScores(ForkJoinExampleNode parent, ForkJoinExampleNode[] childNodes, int depth) {
		for (ForkJoinExampleNode node : childNodes) {
			node.setQuiescent(RANDOM.nextInt(10) == 0);
			if (parent.isQuiescent() && !node.isQuiescent()) {
				node.setQuiescent(RANDOM.nextInt(10) == 0);
			}

			double score = (RANDOM.nextDouble() + (node.isQuiescent() ? RANDOM.nextInt(2) + 1 : 0)) * (node.getPlayer() == TwoPlayers.PLAYER_1 ? 1 : -1);

			node.setScore(parent.getScore() + score);

			if (node.getChildren().length == 0) {
				if (Math.abs(node.getScore()) < 1) {
					node.setScore(AnalysisResult.DRAW);
				} else if (node.getScore() > 0) {
					node.setScore(node.getPlayer() == TwoPlayers.PLAYER_1 ? AnalysisResult.WIN : AnalysisResult.LOSS);
				} else {
					node.setScore(node.getPlayer() == TwoPlayers.PLAYER_1 ? AnalysisResult.LOSS : AnalysisResult.WIN);
				}
			} else {
				setScores(node, node.getChildren(), depth + 1);
			}
		}
	}

	public static ForkJoinExampleNode createTree(int depth, int branchingFactor, boolean playerOne, int number) {
		if (depth < 1) {
			return null;
		} else if (depth == 1) {
			return new ForkJoinExampleNode(playerOne, number, new ForkJoinExampleNode[] {});
		} else {
			int numChildren = branchingFactor;
			ForkJoinExampleNode[] children = new ForkJoinExampleNode[numChildren];
			for (int i = 0; i < numChildren; ++i) {
				children[i] = createTree(depth - 1, branchingFactor, !playerOne, i);
			}
			return new ForkJoinExampleNode(playerOne, number, children);
		}
	}
}
