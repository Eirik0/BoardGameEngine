package analysis;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
		AnalysisResult<M> result = (AnalysisResult<M>) treeSearcher.getResult();
		System.out.println(treeSearcher.getPlies());
		List<MoveWithScore<M>> bestMoves = result.getMovesWithScore().stream().filter(moveWithScore -> moveWithScore.score == result.getMax()).collect(Collectors.toList());
		return bestMoves.get(new Random().nextInt(bestMoves.size())).move;
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
