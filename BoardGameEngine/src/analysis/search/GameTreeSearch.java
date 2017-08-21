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
	private final int player;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;
	private final Consumer<Pair<M, AnalysisResult<M>>> resultConsumer;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean notForked = true;
	private volatile boolean alreadyForked = false;

	public GameTreeSearch(M parentMove, P position, int player, int plies, IDepthBasedStrategy<M, P> strategy,
			Consumer<Pair<M, AnalysisResult<M>>> resultConsumer) {
		this.parentMove = parentMove;
		this.position = position.createCopy();
		this.player = player;
		this.plies = plies;
		this.strategy = strategy.createCopy();
		this.resultConsumer = resultConsumer;
	}

	public synchronized void search() {
		if (plies == 0) {
			result = new AnalysisResult<>(Collections.singletonList(new MoveWithScore<>(parentMove, strategy.evaluate(position, player, plies))));
		} else {
			result = strategy.search(position, player, plies);
		}
		notify();
		if (notForked) {
			alreadyForked = true;
			resultConsumer.accept(Pair.valueOf(parentMove, result));
		}
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
			return position.getPossibleMoves().size();
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
			if (!alreadyForked) { // It seems unlikely that we have already forked, but let's be safe
				resultConsumer.accept(Pair.valueOf(parentMove, strategy.join(position, player, movesWithScore, Collections.emptyList())));
			}
			return Collections.emptyList();
		}

		List<Pair<M, AnalysisResult<M>>> results = new ArrayList<>();
		int expectedResults = unanalyzedMoves.size();

		Consumer<Pair<M, AnalysisResult<M>>> consumerWrapper = moveResult -> {
			synchronized (results) {
				results.add(moveResult);
				if (results.size() == expectedResults) {
					resultConsumer.accept(Pair.valueOf(parentMove, strategy.join(position, player, movesWithScore, results)));
				}
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
}
