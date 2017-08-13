package analysis;

import analysis.search.IterativeDeepeningTreeSearcher;
import game.IPlayer;
import game.IPosition;

public class ComputerPlayer implements IPlayer {
	private final IterativeDeepeningTreeSearcher<?, ?> treeSearcher;
	private final String name;

	public ComputerPlayer(IterativeDeepeningTreeSearcher<?, ?> treeSearcher, String name) {
		this.treeSearcher = treeSearcher;
		this.name = name;
	}

	@Override
	public <M, P extends IPosition<M, P>> M getMove(P position) {
		((IterativeDeepeningTreeSearcher<M, P>) treeSearcher).searchForever(position);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		treeSearcher.stopSearch();
		return (M) treeSearcher.getResult().getBestMove();
	}

	@Override
	public void notifyGameEnded() {
		treeSearcher.stopSearch();
	}

	@Override
	public String toString() {
		return name;
	}
}
