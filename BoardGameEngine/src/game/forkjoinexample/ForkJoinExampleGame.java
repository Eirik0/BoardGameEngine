package game.forkjoinexample;

import analysis.ComputerPlayer;
import game.IGame;
import game.IPlayer;

public class ForkJoinExampleGame implements IGame<ForkJoinExampleNode, ForkJoinExampleTree> {
	public static final int DEPTH = 11;
	public static final int BRANCHING_FACTOR = 2;

	private final ComputerPlayer oneThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 1, "1 Worker", Long.MAX_VALUE);
	private final ComputerPlayer twoThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 2, "2 Workers", Long.MAX_VALUE);
	private final ComputerPlayer threeThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 3, "3 Workers", Long.MAX_VALUE);
	private final ComputerPlayer fourThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 4, "4 Workers", Long.MAX_VALUE);
	private final ComputerPlayer fiveThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 5, "5 Workers", Long.MAX_VALUE);
	private final ComputerPlayer tenThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 10, "10 Workers", Long.MAX_VALUE);
	private final ComputerPlayer thirtySevenThreadPlayer = new ComputerPlayer(new ForkJoinExampleStraregy(), 37, "37 Workers", Long.MAX_VALUE);

	@Override
	public String getName() {
		return "Fork Join Example";
	}

	@Override
	public int getNumberOfPlayers() {
		return 1;
	}

	@Override
	public IPlayer[] getAvailablePlayers() {
		return new IPlayer[] { oneThreadPlayer, twoThreadPlayer, threeThreadPlayer, fourThreadPlayer, fiveThreadPlayer, tenThreadPlayer, thirtySevenThreadPlayer };
	}

	@Override
	public IPlayer getDefaultPlayer() {
		return oneThreadPlayer;
	}

	@Override
	public ForkJoinExampleTree newInitialPosition() {
		ForkJoinExampleTree tree = new ForkJoinExampleTree(DEPTH, BRANCHING_FACTOR);
		ForkJoinExampleThreadTracker.init(tree);
		return tree;
	}
}
