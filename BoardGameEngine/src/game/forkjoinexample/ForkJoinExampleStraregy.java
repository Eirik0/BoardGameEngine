package game.forkjoinexample;

import java.util.Map;

import analysis.AnalysisResult;
import analysis.strategy.ForkJoinObserver;
import analysis.strategy.IAlphaBetaStrategy;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.IForkable;
import game.MoveList;
import game.MoveListFactory;

public class ForkJoinExampleStraregy implements IAlphaBetaStrategy<ForkJoinExampleNode, ForkJoinExampleTree>, ForkJoinObserver<ForkJoinExampleNode> {
	private final IAlphaBetaStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy;

	public ForkJoinExampleStraregy(IAlphaBetaStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy) {
		this.strategy = strategy;
	}

	@Override
	public IForkable<ForkJoinExampleNode, ForkJoinExampleTree> newForkableSearch(ForkJoinExampleNode parentMove, ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> movesToSearch,
			MoveListFactory<ForkJoinExampleNode> moveListFactory, int plies, IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy) {
		return this.strategy.newForkableSearch(parentMove, position, movesToSearch, moveListFactory, plies, strategy);
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int plies) {
		return strategy.evaluate(position, plies);
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int plies, double alpha, double beta) {
		return strategy.evaluate(position, plies, alpha, beta);
	}

	@Override
	public void stopSearch() {
		strategy.stopSearch();
	}

	@Override
	public void join(ForkJoinExampleTree parentPosition, AnalysisResult<ForkJoinExampleNode> partialResult, Map<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>> movesWithResults) {
		strategy.join(parentPosition, partialResult, movesWithResults);
		for (ForkJoinExampleNode move : movesWithResults.keySet()) {
			ForkJoinExampleThreadTracker.branchVisited(parentPosition.getCurrentNode(), move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		ForkJoinExampleThreadTracker.setJoined(parentPosition.getCurrentNode());
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy((IAlphaBetaStrategy<ForkJoinExampleNode, ForkJoinExampleTree>) strategy.createCopy());
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
