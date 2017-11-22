package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;

public class GameTreeSearch<M, P extends IPosition<M, P>> {
	private final M parentMove;
	private final P position;
	private final List<M> possibleMoves;
	private AtomicInteger remainingBranches;
	private final int player;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;
	private Consumer<MoveWithResult<M>> resultConsumer;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean searchStarted = false;
	private volatile boolean searchCanceled = false;
	private volatile boolean forked = false;
	private volatile boolean consumedResult = false;

	public GameTreeSearch(M parentMove, P position, int player, int plies, IDepthBasedStrategy<M, P> strategy, Consumer<MoveWithResult<M>> resultConsumer) {
		this(parentMove, position, player, plies, strategy);
		setResultConsumer(resultConsumer);
	}

	private GameTreeSearch(M parentMove, P position, int player, int plies, IDepthBasedStrategy<M, P> strategy) {
		this.parentMove = parentMove;
		this.position = position.createCopy();
		possibleMoves = this.position.getPossibleMoves();
		remainingBranches = new AtomicInteger(possibleMoves.size());
		this.player = player;
		this.plies = plies;
		this.strategy = strategy.createCopy();
	}

	private void setResultConsumer(Consumer<MoveWithResult<M>> resultConsumer) {
		this.resultConsumer = resultConsumer;
	}

	public synchronized void search() {
		searchStarted = true;
		consumedResult = false;
		if (plies == 0 || possibleMoves.size() == 0) {
			result = new AnalysisResult<>(parentMove, strategy.evaluate(position, player, plies));
			maybeConsumeResult(new MoveWithResult<>(parentMove, result));
		} else {
			result = searchWithStrategy();
			notify();
			if (!forked) {
				maybeConsumeResult(new MoveWithResult<>(parentMove, result));
			}
		}
	}

	private AnalysisResult<M> searchWithStrategy() {
		AnalysisResult<M> analysisResult = new AnalysisResult<>();
		for (M move : possibleMoves) {
			if (searchCanceled && !forked) {
				return analysisResult;
			}
			position.makeMove(move);
			double score = searchCanceled ? 0 : strategy.evaluate(position, player, plies - 1);
			position.unmakeMove(move);
			if (searchCanceled) { // we need to check search canceled after making the call to evaluate
				analysisResult.addUnanalyzedMove(move);
			} else {
				analysisResult.addMoveWithScore(move, score);
			}
			remainingBranches.decrementAndGet();
		}
		return analysisResult;
	}

	private synchronized void maybeConsumeResult(MoveWithResult<M> moveWithResult) {
		if (consumedResult) {
			return;
		}
		consumedResult = true;
		resultConsumer.accept(moveWithResult);
		notify();
	}

	public void stopSearch() {
		searchCanceled = true;
		strategy.stopSearch();
	}

	public int getPlies() {
		return plies;
	}

	public int getRemainingBranches() {
		return remainingBranches.get();
	}

	public List<GameTreeSearch<M, P>> fork() {
		forked = true;
		List<M> unanalyzedMoves;

		if (searchStarted) {
			stopSearch();

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

			if (unanalyzedMoves.isEmpty()) {
				maybeConsumeResult(new MoveWithResult<>(parentMove, result));
				return Collections.emptyList();
			}
		} else {
			result = new AnalysisResult<>();
			unanalyzedMoves = possibleMoves;
		}

		List<MoveWithResult<M>> movesWithResults = new ArrayList<>();
		int expectedResults = unanalyzedMoves.size();

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		synchronized (this) {
			for (M move : unanalyzedMoves) {
				position.makeMove(move);
				gameTreeSearches.add(createFork(move, result, movesWithResults, expectedResults));
				position.unmakeMove(move);
			}
		}

		strategy.notifyForked(parentMove, unanalyzedMoves);

		return gameTreeSearches;
	}

	private GameTreeSearch<M, P> createFork(M move, AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults, int expectedResults) {
		GameTreeSearch<M, P> treeSearch = new GameTreeSearch<M, P>(move, position, player, plies - 1, strategy);
		treeSearch.setResultConsumer(moveWithResult -> {
			synchronized (this) {
				if (consumedResult) {
					return;
				}
				movesWithResults.add(moveWithResult);
				if (treeSearch.searchCanceled && !treeSearch.forked) {
					moveWithResult.invalidate();
					join(partialResult, movesWithResults);
				} else if (!moveWithResult.isValid()) {
					join(partialResult, movesWithResults);
				} else {
					if (movesWithResults.size() == expectedResults) {
						join(partialResult, movesWithResults);
					}
				}
			}
		});
		return treeSearch;
	}

	private synchronized void join(AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults) {
		maybeConsumeResult(join(strategy, parentMove, position, player, partialResult, movesWithResults));
	}

	public static <M, P extends IPosition<M, P>> MoveWithResult<M> join(IDepthBasedStrategy<M, P> strategy, M parentMove, P position, int player, AnalysisResult<M> partialResult,
			List<MoveWithResult<M>> movesWithResults) {
		boolean isValid = true;
		for (MoveWithResult<M> moveWithResult : movesWithResults) {
			strategy.notifyJoined(position, moveWithResult.move);
			// Player and position come from the parent game tree search, so we are looking for the min for the current player
			MoveWithScore<M> moveWithScore = player == position.getCurrentPlayer() ? moveWithResult.result.getMin() : moveWithResult.result.getMax();
			double score;
			if (moveWithScore == null) {
				score = player == position.getCurrentPlayer() ? AnalysisResult.WIN : AnalysisResult.LOSS;
			} else {
				score = moveWithScore.isDraw ? AnalysisResult.DRAW : moveWithScore.score;
			}
			partialResult.addMoveWithScore(moveWithResult.move, score, moveWithResult.isValid());
			isValid = isValid && moveWithResult.isValid();
		}
		return new MoveWithResult<M>(parentMove, partialResult, isValid);
	}
}
