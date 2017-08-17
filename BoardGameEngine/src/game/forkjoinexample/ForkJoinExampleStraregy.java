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
		ForkJoinExampleNode parent = position.getCurrentNode().getParent();
		if (parent != null) {
			ForkJoinExampleThreadTracker.setThreadName(position.getCurrentNode(), 0);
			ForkJoinExampleThreadTracker.branchVisited(parent, position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		}
		visitNodes(position, player, plies);
		return 0;
	}

	private void visitNodes(ForkJoinExampleTree position, int player, int plies) {
		if (searchCancelled) {
			return;
		}

		List<ForkJoinExampleNode> possibleMoves = position.getPossibleMoves();
		if (plies == 0 || possibleMoves.size() == 0) {
			ForkJoinExampleThreadTracker.setThreadName(position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_EVAL);
			return;
		} else {
			ForkJoinExampleNode parent = position.getCurrentNode();
			for (ForkJoinExampleNode move : possibleMoves) {
				position.makeMove(move);
				ForkJoinExampleThreadTracker.branchVisited(parent, position.getCurrentNode(), ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
				visitNodes(position, player, plies - 1);
				position.unmakeMove(move);
			}
		}
	}

	@Override
	public AnalysisResult<ForkJoinExampleNode> join(ForkJoinExampleTree position, int player, List<Pair<ForkJoinExampleNode, Double>> movesWithScore,
			List<Pair<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>>> results) {
		ForkJoinExampleNode currentNode = position.getCurrentNode();
		boolean hasParent = currentNode.getParent() != null;
		if (hasParent) {
			ForkJoinExampleThreadTracker.branchVisited(currentNode.getParent(), currentNode, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		int sleepTime = hasParent ? ForkJoinExampleThreadTracker.SLEEP_PER_MERGE : ForkJoinExampleThreadTracker.SLEEP_PER_EVAL * 16;
		ForkJoinExampleThreadTracker.setThreadName(currentNode, sleepTime);
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
