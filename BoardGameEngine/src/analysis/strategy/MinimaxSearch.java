package analysis.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import analysis.AnalysisResult;
import analysis.search.GameTreeSearch;
import analysis.search.GameTreeSearchJoin;
import analysis.search.IGameTreeSearchJoin;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;

public class MinimaxSearch<M, P extends IPosition<M>> implements IForkable<M, P> {
	public final M parentMove;
	private final P position;
	public final int player;
	private final MoveListFactory<M> moveListFactory;
	private final MoveList<M> movesToSearch;
	private final AtomicInteger branchIndex;
	private final int plies;

	private final IDepthBasedStrategy<M, P> strategy;

	private volatile boolean searchCanceled = false;

	@SuppressWarnings("unchecked")
	public MinimaxSearch(M parentMove, P position, MoveList<M> movesToSearch, MoveListFactory<M> moveListFactory, int plies, IDepthBasedStrategy<M, P> strategy) {
		this.parentMove = parentMove;
		this.position = (P) position.createCopy();
		this.player = this.position.getCurrentPlayer();
		this.movesToSearch = movesToSearch;
		branchIndex = new AtomicInteger(0);
		this.moveListFactory = moveListFactory;
		this.plies = plies;
		this.strategy = strategy.createCopy();
	}

	@Override
	public AnalysisResult<M> search() {
		if (!isForkable()) {
			AnalysisResult<M> result = new AnalysisResult<>(parentMove, strategy.evaluate(position, plies));
			result.searchCompleted();
			return result;
		} else {
			return searchWithStrategy();
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

		if (branchIndex.get() == movesToSearch.size()) {
			analysisResult.searchCompleted();
		}

		return analysisResult;
	}

	@Override
	public void stopSearch() {
		searchCanceled = true;
		strategy.stopSearch();
	}

	@Override
	public int getPlayer() {
		return player;
	}

	@Override
	public M getParentMove() {
		return parentMove;
	}

	@Override
	public int getPlies() {
		return plies;
	}

	@Override
	public int getRemainingBranches() {
		return movesToSearch.size() - branchIndex.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GameTreeSearch<M, P>> fork(IGameTreeSearchJoin<M> parentJoin, AnalysisResult<M> partialResult) {
		if (partialResult == null) {
			partialResult = new AnalysisResult<>();
		}

		MoveList<M> unanalyzedMoves = movesToSearch.subList(partialResult.getMovesWithScore().size());
		int expectedResults = unanalyzedMoves.size();

		if (strategy instanceof ForkJoinObserver<?>) {
			((ForkJoinObserver<M>) strategy).notifyForked(parentMove, unanalyzedMoves);
		}

		List<GameTreeSearch<M, P>> gameTreeSearches = new ArrayList<>();
		GameTreeSearchJoin<M, P> forkJoin = new GameTreeSearchJoin<>(parentJoin, parentMove, position, player, strategy, partialResult, expectedResults);

		int i = 0;
		do {
			M move = unanalyzedMoves.get(i);
			position.makeMove(move);
			MoveList<M> subMoves = moveListFactory.newAnalysisMoveList();
			position.getPossibleMoves(subMoves);
			gameTreeSearches.add(new GameTreeSearch<>(new MinimaxSearch<>(move, position, subMoves, moveListFactory, plies - 1, strategy), forkJoin));
			position.unmakeMove(move);
			++i;
		} while (i < unanalyzedMoves.size());

		return gameTreeSearches;
	}
}
