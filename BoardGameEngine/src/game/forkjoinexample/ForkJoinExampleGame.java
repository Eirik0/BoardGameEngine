package game.forkjoinexample;

import game.IGame;

public class ForkJoinExampleGame implements IGame<ForkJoinExampleNode, ForkJoinExampleTree> {
	public static final String NAME = "Fork Join Example";

	public static final int DEPTH = 10;
	public static final int BRANCHING_FACTOR = 3;
	public static final int MAX_MOVES = BRANCHING_FACTOR;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return 1;
	}

	@Override
	public int getMaxMoves() {
		return MAX_MOVES;
	}

	@Override
	public ForkJoinExampleTree newInitialPosition() {
		ForkJoinExampleTree tree = new ForkJoinExampleTree(DEPTH, BRANCHING_FACTOR);
		ForkJoinExampleThreadTracker.init(tree);
		return tree;
	}
}
