package game.forkjoinexample;

import java.util.List;

import analysis.AbstractDepthBasedStrategy;
import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import util.Pair;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
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
			for (ForkJoinExampleNode move : possibleMoves) {
				position.makeMove(move);
				evaluate(position, player, plies - 1);
				position.unmakeMove(move);
			}
		}
		return 0;
	}

	@Override
	public AnalysisResult<ForkJoinExampleNode> join(ForkJoinExampleTree position, int player, List<Pair<ForkJoinExampleNode, Double>> movesWithScore,
			List<Pair<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>>> results) {
		ForkJoinExampleThreadTracker.setThreadName(position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
