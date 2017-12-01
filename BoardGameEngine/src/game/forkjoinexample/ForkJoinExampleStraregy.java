package game.forkjoinexample;

import java.util.List;

import analysis.AnalysisResult;
import analysis.search.MoveWithResult;
import analysis.strategy.AbstractDepthBasedStrategy;
import analysis.strategy.IDepthBasedStrategy;
import game.MoveList;
import game.MoveListFactory;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	public ForkJoinExampleStraregy(MoveListFactory<ForkJoinExampleNode> moveListFactory) {
		super(moveListFactory);
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
		int numChildren = position.getCurrentNode().getChildren().length;
		if (plies == 0 || numChildren == 0) {
			if (searchCanceled) {
				return;
			}
			ForkJoinExampleThreadTracker.evaluateNode(currentNode);
			return;
		} else {
			MoveList<ForkJoinExampleNode> moveList = getMoveList(plies);
			position.getPossibleMoves(moveList);

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
	public void join(ForkJoinExampleTree parentPosition, int parentPlayer, int currentPlayer, AnalysisResult<ForkJoinExampleNode> partialResult,
			List<MoveWithResult<ForkJoinExampleNode>> movesWithResults) {
		for (MoveWithResult<ForkJoinExampleNode> moveWithResult : movesWithResults) {
			ForkJoinExampleThreadTracker.branchVisited(parentPosition.getCurrentNode(), moveWithResult.move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
			partialResult.addMoveWithScore(moveWithResult.move, moveWithResult.result.getMax().score);
		}
		ForkJoinExampleThreadTracker.setJoined(parentPosition.getCurrentNode());
	}

	@Override
	public void notifyPlyComplete(boolean searchStopped) {
		ForkJoinExampleThreadTracker.searchComplete(searchStopped);
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy(moveListFactory);
	}
}
