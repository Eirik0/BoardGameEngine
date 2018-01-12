package analysis.montecarlo;

import java.util.Random;

import game.MoveList;

public class MonteCarloChildren<M> {
	private static final Random RANDOM = new Random();

	int numUnexpanded;
	int[] unexpandedIndexes;

	public MonteCarloChildren(MoveList<M> moveList) {
		numUnexpanded = moveList.size();
	}

	public void initUnexpanded() {
		unexpandedIndexes = new int[numUnexpanded];
		int i = 0;
		do {
			unexpandedIndexes[i] = i++;
		} while (i < numUnexpanded);
	}

	public int getNextMoveIndex() {
		int moveIndex = RANDOM.nextInt(numUnexpanded);
		int moveListIndex = unexpandedIndexes[moveIndex];
		unexpandedIndexes[moveIndex] = unexpandedIndexes[--numUnexpanded];
		return moveListIndex;
	}

	public int getNextMoveIndex(MoveList<M> moveList) {
		return RANDOM.nextInt(moveList.size());
	}
}
