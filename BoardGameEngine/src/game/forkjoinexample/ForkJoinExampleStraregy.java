package game.forkjoinexample;

import java.util.List;

import util.Pair;
import analysis.AbstractDepthBasedStrategy;
import analysis.AnalysisResult;
import analysis.IDepthBasedStrategy;
import analysis.MoveWithScore;

public class ForkJoinExampleStraregy extends AbstractDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> {
	@Override
	public void notifySearchStarted() {
		ForkJoinExampleThreadTracker.clearInfo();
	}

	@Override
	public void notifyForked(ForkJoinExampleNode parentMove, List<ForkJoinExampleNode> unanalyzedMoves) {
		if (parentMove == null) {
			parentMove = ForkJoinExampleThreadTracker.getRoot();
		}
		ForkJoinExampleThreadTracker.setThreadName(parentMove, 0);
		ForkJoinExampleThreadTracker.setForked(parentMove);
		for (ForkJoinExampleNode move : unanalyzedMoves) {
			ForkJoinExampleThreadTracker.branchVisited(parentMove, move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		}
	}

	@Override
	public void notifySearchComplete() {
		ForkJoinExampleThreadTracker.sleep(ForkJoinExampleThreadTracker.SLEEP_PER_EVAL * 16);
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
			ForkJoinExampleThreadTracker.setThreadName(currentNode, ForkJoinExampleThreadTracker.SLEEP_PER_EVAL);
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
			List<Pair<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>>> results) {
		for (MoveWithScore<ForkJoinExampleNode> moveWithScore : movesWithScore) {
			ForkJoinExampleThreadTracker.branchVisited(position.getCurrentNode(), moveWithScore.move, ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		for (Pair<ForkJoinExampleNode, AnalysisResult<ForkJoinExampleNode>> movesWithResult : results) {
			ForkJoinExampleThreadTracker.branchVisited(position.getCurrentNode(), movesWithResult.getFirst(), ForkJoinExampleThreadTracker.SLEEP_PER_MERGE);
		}
		return new AnalysisResult<>();
	}

	@Override
	public IDepthBasedStrategy<ForkJoinExampleNode, ForkJoinExampleTree> createCopy() {
		return new ForkJoinExampleStraregy();
	}
}
