package game.forkjoinexample;

import java.util.List;

import analysis.AnalysisResult;
import analysis.strategy.AbstractDepthBasedStrategy;
import analysis.strategy.IDepthBasedStrategy;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	@Override
	public double evaluate(ForkJoinExampleTree position, int player, int plies) {
		visitNodes(position, player, plies);
		return 0;
	}

	private void visitNodes(ForkJoinExampleTree position, int player, int plies) {
		if (searchCanceled) {
			return;
		}
		ForkJoinExampleNode currentNode = position.getCurrentNode();
		ForkJoinExampleThreadTracker.branchVisited(currentNode.getParent(), currentNode, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		List<ForkJoinExampleNode> possibleMoves = position.getPossibleMoves();
		if (plies == 0 || possibleMoves.size() == 0) {
			if (searchCanceled) {
				return;
			}
			ForkJoinExampleThreadTracker.evaluateNode(currentNode);
			return;
		} else {
			for (ForkJoinExampleNode move : possibleMoves) {
				if (searchCanceled) {
					return;
				}
				position.makeMove(move);
				visitNodes(position, player, plies - 1);
				position.unmakeMove(move);
			}
		}
	}

	@Override
	public void notifyPlyStarted(AnalysisResult<ForkJoinExampleNode> lastResult) {
		ForkJoinExampleThreadTracker.searchStarted();
	}

	@Override
	public void notifyForked(ForkJoinExampleNode parentMove, List<ForkJoinExampleNode> unanalyzedMoves) {
		if (parentMove == null) {
			parentMove = ForkJoinExampleThreadTracker.getRoot();
		}
		ForkJoinExampleThreadTracker.setForked(parentMove);
		for (ForkJoinExampleNode move : unanalyzedMoves) {
			ForkJoinExampleThreadTracker.branchVisited(parentMove, move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		}
	}

	@Override
	public void notifyJoined(ForkJoinExampleTree parentPosition, ForkJoinExampleNode move) {
		ForkJoinExampleThreadTracker.branchVisited(parentPosition.getCurrentNode(), move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		ForkJoinExampleThreadTracker.setJoined(parentPosition.getCurrentNode());
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		ForkJoinExampleThreadTracker.searchComplete(searchStopped);
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
