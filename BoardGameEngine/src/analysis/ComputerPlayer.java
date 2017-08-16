package analysis;

import analysis.search.IterativeDeepeningTreeSearcher;
import game.IPlayer;
import game.IPosition;

public class ComputerPlayer implements IPlayer {
	private final IterativeDeepeningTreeSearcher<?, ?> treeSearcher;
	private final String name;
	private final long msToPerMove;

	private volatile boolean keepSearching = true;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComputerPlayer(IDepthBasedStrategy<?, ?> strategy, int numWorkers, String name, long msToPerMove) {
		treeSearcher = new IterativeDeepeningTreeSearcher(strategy, numWorkers);
		this.name = name;
		this.msToPerMove = msToPerMove;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <M, P extends IPosition<M, P>> M getMove(P position) {
		((IterativeDeepeningTreeSearcher<M, P>) treeSearcher).searchForever(position);
		long start = System.currentTimeMillis();
		keepSearching = true;
		while (keepSearching && msToPerMove > System.currentTimeMillis() - start) {
			try {
				wait(msToPerMove);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		treeSearcher.stopSearch();
		return (M) treeSearcher.getResult().getBestMove();
	}

	@Override
	public synchronized void notifyGameEnded() {
		treeSearcher.stopSearch();
		keepSearching = false;
		notify();
	}

	@Override
	public String toString() {
		return name;
	}
}
