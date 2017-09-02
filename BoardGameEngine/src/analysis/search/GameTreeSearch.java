package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import analysis.MoveWithScore;
import game.IPosition;

public class GameTreeSearch<M, P extends IPosition<M, P>> {
	private final M parentMove;
	private final P position;
	private final List<M> possibleMoves;
	private volatile int remainingBranches;
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
		remainingBranches = possibleMoves.size();
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
		if (plies == 0) {
			result = new AnalysisResult<>(Collections.singletonList(new MoveWithScore<>(parentMove, strategy.evaluate(position, player, plies))));
			maybeConsumeResult(parentMove, result);
		} else {
			result = searchWithStrategy();
			notify();
			if (!forked) {
				maybeConsumeResult(parentMove, result);
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
			--remainingBranches;
		}
		return analysisResult;
	}

	private synchronized void maybeConsumeResult(M move, AnalysisResult<M> result) {
		if (consumedResult) {
			return;
		}
		consumedResult = true;
		resultConsumer.accept(new MoveWithResult<>(move, result));
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
		return remainingBranches;
	}

	public List<GameTreeSearch<M, P>> fork() {
		forked = true;
		List<M> unanalyzedMoves;
		List<MoveWithScore<M>> movesWithScore;

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
			movesWithScore = result.getMovesWithScore();

			if (unanalyzedMoves.isEmpty()) {
				maybeConsumeResult(parentMove, result);
				return Collections.emptyList();
			}
		} else {
			unanalyzedMoves = possibleMoves;
			movesWithScore = Collections.emptyList();
		}

		List<MoveWithResult<M>> movesWithResults = new ArrayList<>();
		int expectedResults = unanalyzedMoves.size();

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		synchronized (this) {
			for (M move : unanalyzedMoves) {
				position.makeMove(move);
				gameTreeSearches.add(createFork(move, movesWithScore, movesWithResults, expectedResults));
				position.unmakeMove(move);
			}
		}

		strategy.notifyForked(parentMove, unanalyzedMoves);

		return gameTreeSearches;
	}

	private GameTreeSearch<M, P> createFork(M move, List<MoveWithScore<M>> movesWithScore, List<MoveWithResult<M>> movesWithResults, int expectedResults) {
		GameTreeSearch<M, P> treeSearch = new GameTreeSearch<M, P>(move, position, player, plies - 1, strategy);
		treeSearch.setResultConsumer(moveWithResult -> {
			synchronized (this) {
				if (consumedResult) {
					return;
				}
				movesWithResults.add(moveWithResult);
				if ((treeSearch.searchCanceled && !treeSearch.forked) || movesWithResults.size() == expectedResults) {
					join(movesWithScore, movesWithResults);
				}
			}
		});
		return treeSearch;
	}

	private synchronized void join(List<MoveWithScore<M>> movesWithScore, List<MoveWithResult<M>> movesWithResults) {
		maybeConsumeResult(parentMove, join(strategy, position, player, movesWithScore, movesWithResults));
	}

	public static <M, P extends IPosition<M, P>> AnalysisResult<M> join(IDepthBasedStrategy<M, P> strategy, P position, int player, List<MoveWithScore<M>> movesWithScore,
			List<MoveWithResult<M>> movesWithResults) {
		AnalysisResult<M> joinedResult = new AnalysisResult<>(movesWithScore);
		for (MoveWithResult<M> moveWithResult : movesWithResults) {
			joinedResult.addMoveWithScore(moveWithResult.move, strategy.evaluateJoin(position, player, moveWithResult));
		}
		return joinedResult;
	}
}
