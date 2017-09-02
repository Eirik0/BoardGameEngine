package game.forkjoinexample;

import java.util.List;

import analysis.AbstractDepthBasedStrategy;
import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import analysis.MoveWithScore;
import analysis.search.MoveWithResult;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	@Override
	public void notifySearchStarted() {
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
	public void notifySearchComplete() {
		ForkJoinExampleThreadTracker.searchComplete();
	}

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
	public AnalysisResult<ForkJoinExampleNode> join(ForkJoinExampleTree position, int player, List<MoveWithScore<ForkJoinExampleNode>> movesWithScore,
			List<MoveWithResult<ForkJoinExampleNode>> movesWithResults) {
		for (MoveWithResult<ForkJoinExampleNode> movesWithResult : movesWithResults) {
			ForkJoinExampleThreadTracker.branchVisited(position.getCurrentNode(), movesWithResult.move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		ForkJoinExampleThreadTracker.setJoined(position.getCurrentNode());
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
