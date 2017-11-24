package game.forkjoinexample;

import analysis.AnalysisResult;
import analysis.strategy.AbstractDepthBasedStrategy;
import analysis.strategy.IDepthBasedStrategy;
import game.ArrayMoveList;
import game.MoveList;

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
		int numChildren = position.getCurrentNode().getChildren().length;
		MoveList<ForkJoinExampleNode> moveList = new ArrayMoveList<>(numChildren);
		position.getPossibleMoves(moveList);
		if (plies == 0 || numChildren == 0) {
			if (searchCanceled) {
				return;
			}
			ForkJoinExampleThreadTracker.evaluateNode(currentNode);
			return;
		} else {
			int i = 0;
			while (i < numChildren) {
				ForkJoinExampleNode move = moveList.get(i);
				if (searchCanceled) {
					return;
				}
				position.makeMove(move);
				visitNodes(position, player, plies - 1);
				position.unmakeMove(move);
				++i;
			}
		}
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
		int i = 0;
		while (i < unanalyzedMoves.size()) {
			ForkJoinExampleNode move = unanalyzedMoves.get(i);
			ForkJoinExampleThreadTracker.branchVisited(parentMove, move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
			++i;
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
