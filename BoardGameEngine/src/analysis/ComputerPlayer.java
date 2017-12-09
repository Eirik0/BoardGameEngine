package analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import analysis.search.IterativeDeepeningTreeSearcher;
import analysis.strategy.IDepthBasedStrategy;
import game.IPlayer;
import game.IPosition;
import game.MoveListFactory;
import gui.analysis.ComputerPlayerResult;

public class ComputerPlayer implements IPlayer {
	public static final String NAME = "Computer";

	private final String strategyName;

	private final IterativeDeepeningTreeSearcher<?, ?> treeSearcher;
	private final int numWorkers;
	private final long msPerMove;
	private final boolean escapeEarly;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ComputerPlayer(String strategyName, IDepthBasedStrategy<?, ?> strategy, MoveListFactory<?> moveListFactory, int numWorkers, long msPerMove, boolean escapeEarly) {
		this.strategyName = strategyName;
		treeSearcher = new IterativeDeepeningTreeSearcher(strategy, moveListFactory, numWorkers);
		this.numWorkers = numWorkers;
		this.msPerMove = msPerMove;
		this.escapeEarly = escapeEarly;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <M, P extends IPosition<M, P>> M getMove(P position) {
		long start = System.currentTimeMillis();
		((IterativeDeepeningTreeSearcher<M, P>) treeSearcher).searchForever(position, escapeEarly);
		while (treeSearcher.isSearching() && msPerMove > System.currentTimeMillis() - start) {
			try {
				wait(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (treeSearcher.isSearching()) {
			treeSearcher.stopSearch(false);
		}

		AnalysisResult<M> result = (AnalysisResult<M>) treeSearcher.getResult();
		MoveWithScore<M> bestMoveWithScore = result == null ? null : result.getMax();

		if (bestMoveWithScore == null) {
			return null;
		}

		List<M> bestMoves = new ArrayList<>();

		if (AnalysisResult.isDraw(bestMoveWithScore.score)) {
			for (MoveWithScore<M> moveWithScore : result.getMovesWithScore()) {
				if (AnalysisResult.isDraw(moveWithScore.score)) {
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
		stopSearch(true);
		notify();
	}

	public void stopSearch(boolean gameEnded) {
		treeSearcher.stopSearch(gameEnded);
	}

	@Override
	public String toString() {
		return ComputerPlayerInfo.getComputerName(strategyName, numWorkers, msPerMove);
	}

	@SuppressWarnings("unchecked")
	public ComputerPlayerResult getCurrentResult() {
		List<?> partialResult = treeSearcher.getPartialResult();
		return new ComputerPlayerResult((AnalysisResult<Object>) treeSearcher.getResult(), (List<MoveWithScore<Object>>) partialResult, treeSearcher.getPlies());
	}
}
