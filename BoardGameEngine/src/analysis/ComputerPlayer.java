package analysis;

import analysis.search.IterativeDeepeningTreeSearcher;
import analysis.strategy.IDepthBasedStrategy;
import game.IPlayer;
import game.IPosition;

public class ComputerPlayer implements IPlayer {
	private final IterativeDeepeningTreeSearcher<?, ?> treeSearcher;
	private final String name;
	private final long msPerMove;

	private volatile boolean keepSearching = true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComputerPlayer(IDepthBasedStrategy<?, ?> strategy, int numWorkers, String name, long msPerMove) {
		treeSearcher = new IterativeDeepeningTreeSearcher(strategy, numWorkers);
		this.name = name;
		this.msPerMove = msPerMove;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <M, P extends IPosition<M, P>> M getMove(P position) {
		long start = System.currentTimeMillis();
		((IterativeDeepeningTreeSearcher<M, P>) treeSearcher).searchForever(position);
		keepSearching = true;
		while (treeSearcher.isSearching() && keepSearching && msPerMove > System.currentTimeMillis() - start) {
			try {
				wait(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		treeSearcher.stopSearch(false);
		return (M) treeSearcher.getResult().getBestMove();
	}

	@Override
	public synchronized void notifyGameEnded() {
		treeSearcher.stopSearch(true);
		keepSearching = false;
		notify();
	}

	@Override
	public String toString() {
		return name;
	}
}
