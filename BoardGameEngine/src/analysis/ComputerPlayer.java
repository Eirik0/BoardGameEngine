package analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import analysis.search.IterativeDeepeningTreeSearcher;
import analysis.strategy.IDepthBasedStrategy;
import game.IPlayer;
import game.IPosition;
import gui.analysis.ComputerPlayerResult;

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
		MoveWithScore<M> bestMoveWithScore = result.getMax();

		List<M> bestMoves = new ArrayList<>();

		if (bestMoveWithScore.isDraw) {
			for (MoveWithScore<M> moveWithScore : result.getMovesWithScore()) {
				if (moveWithScore.isDraw) {
					bestMoves.add(moveWithScore.move);
				}
			}
		} else {
			for (MoveWithScore<M> moveWithScore : result.getMovesWithScore()) {
				if (bestMoveWithScore.score == moveWithScore.score) {
					bestMoves.add(moveWithScore.move);
				}
			}
		}

		return bestMoves.get(new Random().nextInt(bestMoves.size()));
	}

	@Override
	public void notifyTurnEnded() {
		treeSearcher.clearResult();
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

	@SuppressWarnings("unchecked")
	public ComputerPlayerResult getCurrentResult() {
		return new ComputerPlayerResult((AnalysisResult<Object>) treeSearcher.getResult(), treeSearcher.getPlies());
	}
}
