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
	public void notifyForked(ForkJoinExampleNode parentMove, List<ForkJoinExampleNode> unanalyzedMoves) {
		if (parentMove == null) {
			parentMove = ForkJoinExampleThreadTracker.nodesByDepth().get(0).get(0);
		}
		ForkJoinExampleThreadTracker.setThreadName(parentMove, 0);
		ForkJoinExampleThreadTracker.setForked(parentMove);
		for (ForkJoinExampleNode move : unanalyzedMoves) {
			ForkJoinExampleThreadTracker.branchVisited(parentMove, move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		}
	}

	@Override
	public double evaluate(ForkJoinExampleTree position, int player, int plies) {
		ForkJoinExampleNode parent = position.getCurrentNode().getParent();
		if (parent != null) {
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
		} else {
			ForkJoinExampleThreadTracker.sleep(ForkJoinExampleThreadTracker.SLEEP_PER_EVAL * 16);
		}
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
