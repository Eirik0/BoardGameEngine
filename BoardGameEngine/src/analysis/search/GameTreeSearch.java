package analysis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import analysis.AnalysisResult;
import analysis.strategy.IDepthBasedStrategy;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class GameTreeSearch<M, P extends IPosition<M>> {
	public final M parentMove;
	private final P position;
	public final int player;
	private final MoveListFactory<M> moveListFactory;
	private final MoveList<M> movesToSearch;
	private final AtomicInteger branchIndex;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;
	private final IGameTreeSearchJoin<M> treeSearchJoin;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean searchStarted = false;
	private volatile boolean searchCanceled = false;

	private final AtomicBoolean forked = new AtomicBoolean(false);
	private final AtomicBoolean joined = new AtomicBoolean(false);

	@SuppressWarnings("unchecked")
	public GameTreeSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy,
			IGameTreeSearchJoin<M> treeSearchJoin) {
		this.parentMove = parentMove;
		this.position = (P) position.createCopy();
		this.player = this.position.getCurrentPlayer();
		this.moveListFactory = moveListFactory;
		this.movesToSearch = movesToSearch;
		branchIndex = new AtomicInteger(0);
		this.plies = plies;
		this.strategy = strategy.createCopy();
		this.treeSearchJoin = treeSearchJoin;
	}

	public synchronized void search() {
		if (!isForkable()) {
			result = new AnalysisResult<>(parentMove, strategy.evaluate(position, plies));
			result.searchCompleted();
			treeSearchJoin.accept(false, position.getCurrentPlayer(), new MoveWithResult<>(parentMove, result));
		} else if (!forked.get()) {
			searchStarted = true;
			result = searchWithStrategy();
			notify();
			if (!forked.get()) {
				joined.set(true);
				treeSearchJoin.accept(searchCanceled, position.getCurrentPlayer(), new MoveWithResult<>(parentMove, result));
			}
		}
	}

	private synchronized AnalysisResult<M> searchWithStrategy() {
		AnalysisResult<M> analysisResult = new AnalysisResult<>();
		do {
			M move = movesToSearch.get(branchIndex.get());
			position.makeMove(move);
			strategy.preSearch(analysisResult, player == position.getCurrentPlayer());
			double evaluate = strategy.evaluate(position, plies - 1);
			double score = searchCanceled ? 0 : player == position.getCurrentPlayer() ? evaluate : -evaluate;
			position.unmakeMove(move);
			if (searchCanceled) { // we need to check search canceled after making the call to evaluate
				break;
			} else {
				analysisResult.addMoveWithScore(move, score);
			}
		} while (branchIndex.incrementAndGet() < movesToSearch.size());

		if (!searchCanceled) {
			analysisResult.searchCompleted();
		}

		return analysisResult;
	}

	public AnalysisResult<M> getResult() {
		return result;
	}

	public void stopSearch() {
		searchCanceled = true;
		strategy.stopSearch();
	}

	public int getPlies() {
		return plies;
	}

	public int getRemainingBranches() {
		return movesToSearch.size() - branchIndex.get();
	}

	public boolean isForkable() {
		return plies > 0 && getRemainingBranches() > 0;
	}

	public List<GameTreeSearch<M, P>> fork() {
		forked.set(true);
		if (joined.get()) {
			return Collections.emptyList();
		}

		stopSearch();

		MoveList<M> unanalyzedMoves;

		if (searchStarted) {
			synchronized (this) {
				while (result == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}

			unanalyzedMoves = movesToSearch.subList(result.getMovesWithScore().size());
		} else {
			unanalyzedMoves = movesToSearch;
			if (movesToSearch.size() == 0) {
				result = new AnalysisResult<>(parentMove, strategy.evaluate(position, plies));
			} else {
				result = new AnalysisResult<>();
			}
		}

		if (unanalyzedMoves.size() == 0) {
			synchronized (this) {
				if (!joined.getAndSet(true)) {
					result.searchCompleted();
					treeSearchJoin.accept(false, player, new MoveWithResult<>(parentMove, result));
				}
				return Collections.emptyList();
			}
		}

		int expectedResults = unanalyzedMoves.size();

		strategy.notifyForked(parentMove, unanalyzedMoves);

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		synchronized (this) {
			GameTreeSearchJoin<M, P> forkJoin = new GameTreeSearchJoin<>(treeSearchJoin, parentMove, position, player, strategy, result, expectedResults);
			int i = 0;
			do {
				M move = unanalyzedMoves.get(i);
				position.makeMove(move);
				MoveList<M> subMoves = moveListFactory.newAnalysisMoveList();
				position.getPossibleMoves(subMoves);
				gameTreeSearches.add(new GameTreeSearch<>(move, position, subMoves, moveListFactory, plies - 1, strategy, forkJoin));
				position.unmakeMove(move);
				++i;
			} while (i < unanalyzedMoves.size());
		}
		return gameTreeSearches;
	}
}
