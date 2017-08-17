package game.forkjoinexample;

import java.util.List;

import analysis.AbstractDepthBasedStrategy;
import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import util.Pair;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	@Override
	public void notifySearchStarted() {
		ForkJoinExampleThreadTracker.clearInfo();
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int player, int plies) {
		if (searchCancelled) {
			return 0;
		}

		List<ForkJoinExampleNode> possibleMoves = position.getPossibleMoves();
		if (plies == 0 || possibleMoves.size() == 0) {
			ForkJoinExampleThreadTracker.setThreadName(position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_EVAL);
			return 0;
		} else {
			ForkJoinExampleNode parent = position.getCurrentNode();
			for (ForkJoinExampleNode move : possibleMoves) {
				position.makeMove(move);
				ForkJoinExampleThreadTracker.branchVisited(parent, position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
				evaluate(position, player, plies - 1);
				position.unmakeMove(move);
			}
		}
		return 0;
	}

	@Override
	public AnalysisResult<ForkJoinExampleNode> join(ForkJoinExampleTree position, int player, List<Pair<ForkJoinExampleNode, Double>> movesWithScore,
			List<Pair<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>>> results) {
		for (Pair<ForkJoinExampleNode, Double> moveWithScore : movesWithScore) {
			ForkJoinExampleThreadTracker.branchVisited(position.getCurrentNode(), moveWithScore.getFirst(), ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		ForkJoinExampleThreadTracker.setThreadName(position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
