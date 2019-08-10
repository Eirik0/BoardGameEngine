package game.forkjoinexample;

import analysis.IPositionEvaluator;
import game.MoveList;

public class ForkJoinPositionEvaluator implements IPositionEvaluator<ForkJoinExampleNode, ForkJoinExampleTree> {
	@Override
	public double evaluate(ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> possibleMoves) {
		ForkJoinExampleThreadTracker.evaluateNode(position.getCurrentNode());
		return position.getCurrentNode().getScore();
	}
}
