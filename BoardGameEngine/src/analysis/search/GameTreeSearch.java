package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import analysis.AnalysisResult;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class GameTreeSearch<M, P extends IPosition<M, P>> {
	public final M parentMove;
	private final P position;
	private MoveListFactory<M> moveListFactory;
	private final MoveList<M> possibleMoves;
	private AtomicInteger branchIndex;
	private final int player;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;
	private Consumer<MoveWithResult<M>> resultConsumer;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean searchStarted = false;
	private volatile boolean searchCanceled = false;
	private volatile boolean forked = false;
	private final AtomicBoolean consumedResult = new AtomicBoolean(false);

	public GameTreeSearch(M parentMove, P position, MoveListFactory<M> moveListFactory, int player, int plies, IDepthBasedStrategy<M, P> strategy, Consumer<MoveWithResult<M>> resultConsumer) {
		this(parentMove, position, moveListFactory, player, plies, strategy);
		setResultConsumer(resultConsumer);
	}

	private GameTreeSearch(M parentMove, P position, MoveListFactory<M> moveListFactory, int player, int plies, IDepthBasedStrategy<M, P> strategy) {
		this.parentMove = parentMove;
		this.position = position.createCopy();
		this.moveListFactory = moveListFactory;
		possibleMoves = moveListFactory.newAnalysisMoveList();
		this.position.getPossibleMoves(possibleMoves);
		branchIndex = new AtomicInteger(0);
		this.player = player;
		this.plies = plies;
		this.strategy = strategy.createCopy();
	}

	private void setResultConsumer(Consumer<MoveWithResult<M>> resultConsumer) {
		this.resultConsumer = resultConsumer;
	}

	public synchronized void search() {
		searchStarted = true;
		consumedResult.set(false);
		if (plies == 0 || possibleMoves.size() == 0) {
			double evaluate = strategy.evaluate(position, plies);
			result = new AnalysisResult<>(parentMove, player == position.getCurrentPlayer() ? evaluate : -evaluate);
			result.searchCompleted();
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

		do {
			M move = possibleMoves.get(branchIndex.get());
			if (searchCanceled && !forked) {
				return analysisResult;
			}
			position.makeMove(move);
			double evaluate = strategy.evaluate(position, plies - 1);
			double score = searchCanceled ? 0 : player == position.getCurrentPlayer() ? evaluate : -evaluate;
			position.unmakeMove(move);
			if (searchCanceled) { // we need to check search canceled after making the call to evaluate
				break;
			} else {
				analysisResult.addMoveWithScore(move, score);
			}
		} while (branchIndex.incrementAndGet() < possibleMoves.size());
		if (!searchCanceled) {
			analysisResult.searchCompleted();
		}
		return analysisResult;
	}

	public AnalysisResult<M> getResult() {
		return result;
	}

	private synchronized void maybeConsumeResult(MoveWithResult<M> moveWithResult) {
		if (consumedResult.getAndSet(true)) {
			return;
		}
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
		return possibleMoves.size() - branchIndex.get();
	}

	public List<GameTreeSearch<M, P>> fork() {
		forked = true;
		MoveList<M> unanalyzedMoves;

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

			unanalyzedMoves = possibleMoves.subList(result.getMovesWithScore().size());
		} else {
			unanalyzedMoves = possibleMoves;
			if (possibleMoves.size() == 0) {
				double evaluate = strategy.evaluate(position, plies);
				result = new AnalysisResult<>(parentMove, player == position.getCurrentPlayer() ? evaluate : -evaluate);
			} else {
				result = new AnalysisResult<>();
			}
		}

		if (unanalyzedMoves.size() == 0) {
			result.searchCompleted();
			maybeConsumeResult(new MoveWithResult<>(parentMove, result));
			return Collections.emptyList();
		}

		List<MoveWithResult<M>> movesWithResults = new ArrayList<>();
		int expectedResults = unanalyzedMoves.size();

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		synchronized (this) {
			int i = 0;
			do {
				M move = unanalyzedMoves.get(i);
				position.makeMove(move);
				gameTreeSearches.add(createFork(move, result, movesWithResults, expectedResults));
				position.unmakeMove(move);
				++i;
			} while (i < unanalyzedMoves.size());
		}

		strategy.notifyForked(parentMove, unanalyzedMoves);

		return gameTreeSearches;
	}

	private GameTreeSearch<M, P> createFork(M move, AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults, int expectedResults) {
		GameTreeSearch<M, P> treeSearch = new GameTreeSearch<>(move, position, moveListFactory, player, plies - 1, strategy);
		int currentPlayer = position.getCurrentPlayer();
		treeSearch.setResultConsumer(moveWithResult -> {
			if (consumedResult.get()) {
				return;
			}
			synchronized (this) {
				movesWithResults.add(moveWithResult);
				if (treeSearch.searchCanceled && !treeSearch.forked) {
					join(currentPlayer, partialResult, movesWithResults);
				} else if (!moveWithResult.result.isSeachComplete()) {
					join(currentPlayer, partialResult, movesWithResults);
				} else {
					if (movesWithResults.size() == expectedResults) {
						partialResult.searchCompleted();
						join(currentPlayer, partialResult, movesWithResults);
					}
				}
			}
		});
		return treeSearch;
	}

	private synchronized void join(int currentPlayer, AnalysisResult<M> partialResult, List<MoveWithResult<M>> movesWithResults) {
		strategy.join(position, player, currentPlayer, partialResult, movesWithResults);
		maybeConsumeResult(new MoveWithResult<>(parentMove, partialResult));
	}
}
