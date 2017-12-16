package game.forkjoinexample;

import java.util.Map;

import analysis.AnalysisResult;
import analysis.strategy.AbstractDepthBasedStrategy;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.MinimaxStrategy;
import game.MoveList;
import game.MoveListFactory;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	private final IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy;

	public ForkJoinExampleStraregy(MoveListFactory<ForkJoinExampleNode> moveListFactory) {
		this(moveListFactory, new MinimaxStrategy<>(moveListFactory, new ForkJoinPositionEvaluator()));
	}

	public ForkJoinExampleStraregy(MoveListFactory<ForkJoinExampleNode> moveListFactory, IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> strategy) {
		super(moveListFactory);
		this.strategy = strategy;
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int plies) {
		return strategy.evaluate(position, plies);
	}

	@Override
	public void stopSearch() {
		strategy.stopSearch();
		super.stopSearch();
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
	public void join(ForkJoinExampleTree parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<ForkJoinExampleNode> partialResult,
			Map<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>> movesWithResults) {
		strategy.join(parentPosition, parentPlayer, currentPlayer, partialResult, movesWithResults);
		for (ForkJoinExampleNode move : movesWithResults.keySet()) {
			ForkJoinExampleThreadTracker.branchVisited(parentPosition.getCurrentNode(), move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		ForkJoinExampleThreadTracker.setJoined(parentPosition.getCurrentNode());
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		ForkJoinExampleThreadTracker.searchComplete(searchStopped);
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy(moveListFactory, strategy.createCopy());
	}
}
