package analysis.montecarlo;

import java.util.Random;

import game.IPosition;
import game.MoveList;

public class RandomMonteCarloChildren<M> implements IMonteCarloChildren<M> {
	private static final Random RANDOM = new Random();

	int numUnexpanded;
	int[] unexpandedIndexes;

	public RandomMonteCarloChildren(int numUnexpanded) {
		this.numUnexpanded = numUnexpanded;
	}

	@Override
	public IMonteCarloChildren<M> createNewWith(int numUnexpanded) {
		return new RandomMonteCarloChildren<>(numUnexpanded);
	}

	@Override
	public <P extends IPosition<M>> boolean initUnexpanded(MonteCarloGameNode<M, P> parentNode) {
		unexpandedIndexes = new int[numUnexpanded];
		int i = 0;
		do {
			unexpandedIndexes[i] = i++;
		} while (i < numUnexpanded);
		return true;
	}

	@Override
	public int getNumUnexpanded() {
		return numUnexpanded;
	}

	@Override
	public void setNumUnexpanded(int numUnexpanded) {
		this.numUnexpanded = numUnexpanded;
	}

	@Override
	public int getNextNodeIndex() {
		int moveIndex = RANDOM.nextInt(numUnexpanded);
		int moveListIndex = unexpandedIndexes[moveIndex];
		unexpandedIndexes[moveIndex] = unexpandedIndexes[--numUnexpanded];
		return moveListIndex;
	}

	@Override
	public int getNextMoveIndex(MoveList<M> moveList) {
		return RANDOM.nextInt(moveList.size());
	}
}
