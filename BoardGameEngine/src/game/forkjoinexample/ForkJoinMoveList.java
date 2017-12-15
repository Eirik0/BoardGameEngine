package game.forkjoinexample;

import game.ArrayMoveList;
import game.IPosition;
import game.MoveList;

public class ForkJoinMoveList implements MoveList<ForkJoinExampleNode> {
	private final ArrayMoveList<ForkJoinExampleNode> arrayMoveList;

	public ForkJoinMoveList(int capacity) {
		arrayMoveList = new ArrayMoveList<>(capacity);
	}

	@Override
	public <P extends IPosition<ForkJoinExampleNode, P>> void addDynamicMove(ForkJoinExampleNode move, P position) {
		ForkJoinExampleThreadTracker.branchVisited(move.getParent(), move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		arrayMoveList.addDynamicMove(move, position);
	}

	@Override
	public <P extends IPosition<ForkJoinExampleNode, P>> void addQuietMove(ForkJoinExampleNode move, P position) {
		ForkJoinExampleThreadTracker.branchVisited(move.getParent(), move, ForkJoinExampleThreadTracker.SLEEP_PER_BRANCH);
		arrayMoveList.addQuietMove(move, position);
	}

	@Override
	public <P extends IPosition<ForkJoinExampleNode, P>> void addAllQuietMoves(ForkJoinExampleNode[] moves, P position) {
		int i = 0;
		while (i < moves.length) {
			addQuietMove(moves[i], position);
			++i;
		}
	}

	@Override
	public ForkJoinExampleNode get(int index) {
		return arrayMoveList.get(index);
	}

	@Override
	public boolean contains(ForkJoinExampleNode move) {
		return arrayMoveList.contains(move);
	}

	@Override
	public int size() {
		return arrayMoveList.size();
	}

	@Override
	public int numDynamicMoves() {
		return arrayMoveList.numDynamicMoves();
	}

	@Override
	public MoveList<ForkJoinExampleNode> subList(int beginIndex) {
		return arrayMoveList.subList(beginIndex);
	}

	@Override
	public void clear() {
		arrayMoveList.clear();
	}
}
