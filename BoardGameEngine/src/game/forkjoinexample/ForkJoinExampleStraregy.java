package game.forkjoinexample;

import java.util.Map;

import analysis.AnalysisResult;
import analysis.strategy.ForkJoinObserver;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.IForkable;
import analysis.strategy.MinimaxStrategy;
import analysis.strategy.MoveListProvider;
import game.MoveList;
import game.MoveListFactory;

public class ForkJoinExampleStraregy implements IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree>, ForkJoinObserver<ForkJoinExampleNode> {
	private final IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy;

	public ForkJoinExampleStraregy(MoveListProvider<ForkJoinExampleNode> moveListProvider) {
		this(new MinimaxStrategy<>(new ForkJoinPositionEvaluator(), moveListProvider));
	}

	public ForkJoinExampleStraregy(IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy) {
		this.strategy = strategy;
	}

	@Override
	public IForkable<ForkJoinExampleNode, ForkJoinExampleTree> newForkableSearch(ForkJoinExampleNode parentMove, ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> movesToSearch,
			MoveListFactory<ForkJoinExampleNode> moveListFactory, int plies, IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy) {
		return this.strategy.newForkableSearch(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	public void preSearch(AnalysisResult<ForkJoinExampleNode> currentResult, boolean isCurrentPlayer) {
		// do nothing
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int plies) {
		return strategy.evaluate(position, plies);
	}

	@Override
	public void stopSearch() {
		strategy.stopSearch();
	}

	@Override
	public void join(ForkJoinExampleTree parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<ForkJoinExampleNode> partialResult,
			Map<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>> movesWithResults) {
		strategy.join(parentPosition, parentPlayer, currentPlayer, partialResult, movesWithResults);
		for (ForkJoinExampleNode move : movesWithResults.keySet()) {
			ForkJoinExampleThreadTracker.branchVisited(parentPosition.getCurrentNode(), move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		ForkJoinExampleThreadTracker.setJoined(parentPosition.getCurrentNode());
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy(strategy.createCopy());
	}

	@Override
	public void notifyPlyStarted(AnalysisResult<ForkJoinExampleNode> lastResult) {
		ForkJoinExampleThreadTracker.searchStarted();
	}

	@Override
	public void notifyForked(ForkJoinExampleNode parentMove, MoveList<ForkJoinExampleNode> unanalyzedMoves) {
		if (parentMove == null) {
			parentMove = ForkJoinExampleThreadTracker.getRoot();
		}
		ForkJoinExampleThreadTracker.setForked(parentMove);
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		ForkJoinExampleThreadTracker.searchComplete(searchStopped);
	}
}
