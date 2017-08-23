package analysis.search;

import game.IPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import util.Pair;
import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import analysis.MoveWithScore;

public class GameTreeSearch<M, P extends IPosition<M, P>> {
	private final M parentMove;
	private final P position;
	private final int numBranches;
	private final int player;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;
	private final Consumer<Pair<M, AnalysisResult<M>>> resultConsumer;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean notForked = true;
	private volatile boolean consumedResult = false;

	public GameTreeSearch(M parentMove, P position, int player, int plies, IDepthBasedStrategy<M, P> strategy, Consumer<Pair<M, AnalysisResult<M>>> resultConsumer) {
		this.parentMove = parentMove;
		this.position = position.createCopy();
		numBranches = this.position.getPossibleMoves().size();
		this.player = player;
		this.plies = plies;
		this.strategy = strategy.createCopy();
		this.resultConsumer = resultConsumer;
	}

	public synchronized void search() {
		consumedResult = false;
		P positionCopy = position.createCopy();
		if (plies == 0) {
			result = new AnalysisResult<>(Collections.singletonList(new MoveWithScore<>(parentMove, strategy.evaluate(positionCopy, player, plies))));
		} else {
			result = strategy.search(positionCopy, player, plies);
		}
		if (notForked) {
			maybeConsumeResult(parentMove, result);
		}
		notify();
	}

	public void stopSearch() {
		strategy.stopSearch();
	}

	public int getPlies() {
		return plies;
	}

	public int getRemainingBranches() {
		if (strategy.isSearching()) {
			return strategy.getRemainingBranches();
		} else if (result != null) { // search started and finished
			return 0;
		} else {
			return numBranches;
		}
	}

	public boolean isForkable() {
		return plies > 0 && getRemainingBranches() > 0;
	}

	public List<GameTreeSearch<M, P>> fork() {
		notForked = false;
		List<M> unanalyzedMoves;
		List<MoveWithScore<M>> movesWithScore;

		if (strategy.isSearching()) {
			strategy.stopSearch();

			synchronized (this) {
				while (result == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}

			unanalyzedMoves = result.getUnanalyzedMoves();
			movesWithScore = result.getMovesWithScore();
		} else {
			unanalyzedMoves = position.getPossibleMoves();
			movesWithScore = Collections.emptyList();
		}

		if (unanalyzedMoves.isEmpty()) {
			AnalysisResult<M> joinedResult = strategy.join(position, player, movesWithScore, Collections.emptyList());
			maybeConsumeResult(parentMove, joinedResult);
			return Collections.emptyList();
		}

		List<Pair<M, AnalysisResult<M>>> results = Collections.synchronizedList(new ArrayList<>());
		int expectedResults = unanalyzedMoves.size();

		Consumer<Pair<M, AnalysisResult<M>>> consumerWrapper = moveResult -> {
			results.add(moveResult);
			if (results.size() == expectedResults) {
				AnalysisResult<M> joinedResult = strategy.join(position, player, movesWithScore, results);
				maybeConsumeResult(parentMove, joinedResult);
			}
		};

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		for (M move : unanalyzedMoves) {
			position.makeMove(move);
			gameTreeSearches.add(new GameTreeSearch<M, P>(move, position, player, plies - 1, strategy, consumerWrapper));
			position.unmakeMove(move);
		}
		strategy.notifyForked(parentMove, unanalyzedMoves);
		return gameTreeSearches;
	}

	private synchronized void maybeConsumeResult(M move, AnalysisResult<M> result) {
		if (consumedResult) {
			return;
		}
		consumedResult = true;
		resultConsumer.accept(Pair.valueOf(move, result));
		notify();
	}
}
