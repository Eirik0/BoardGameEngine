package analysis.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import analysis.strategy.MoveListProvider;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import game.forkjoinexample.ForkObserver;

public class MonteCarloGameNode<M, P extends IPosition<M>> {
	private static final double LOSS = -1;

	private static final Random RANDOM = new Random();

	private MonteCarloGameNode<M, P> parentNode;

	final M parentMove;
	private final P position;
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListFactory<M> moveListFactory;
	private MoveListProvider<M> moveListProdiver;
	private final int numSimulations;

	private final MoveList<M> moveList;
	private int numUnexpanded;
	int[] unexpandedIndexes;

	List<MonteCarloGameNode<M, P>> expandedChildren;

	final MonteCarloStatistics statistics;

	private final ForkObserver<M> expandObserver;

	private boolean isSearching = false;
	private volatile boolean stopRequested = false;

	public MonteCarloGameNode(M parentMove, P position, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSumulations) {
		this(null, parentMove, position, positionEvaluator, moveListFactory, numSumulations, null);
	}

	public MonteCarloGameNode(MonteCarloGameNode<M, P> parentNode, M parentMove, P position, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSumulations,
			ForkObserver<M> expandObserver) {
		this.parentNode = parentNode;
		this.parentMove = parentMove;
		this.position = position;
		this.positionEvaluator = positionEvaluator;
		this.moveListFactory = moveListFactory;
		this.numSimulations = numSumulations;
		this.expandObserver = expandObserver;
		moveListProdiver = new MoveListProvider<>(moveListFactory);
		moveList = moveListFactory.newAnalysisMoveList();
		position.getPossibleMoves(moveList);
		numUnexpanded = moveList.size();
		statistics = new MonteCarloStatistics(position.getCurrentPlayer());
	}

	public void searchRoot() {
		isSearching = true;
		if (moveList.size() == 0) {
			statistics.updateWith(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, moveList)));
			statistics.setDecided();
		} else {
			initUnexpanded();

			do {
				if (numUnexpanded == 0) {
					select();
				} else {
					expand();
				}
			} while (!stopRequested && !statistics.isDecided);
		}
		synchronized (this) {
			isSearching = false;
			notify();
		}
	}

	private void initUnexpanded() {
		expandedChildren = new ArrayList<>();
		unexpandedIndexes = new int[numUnexpanded];
		int i = 0;
		do {
			unexpandedIndexes[i] = i++;
		} while (i < numUnexpanded);
	}

	private void search() {
		if (expandedChildren == null) {
			if (moveList.size() == 0) {
				backPropagate(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, moveList)), true);
			} else {
				initUnexpanded();
				expand();
			}
		} else if (numUnexpanded > 0) {
			expand();
		} else {
			select();
		}
	}

	public AnalysisResult<M> getResult() {
		if (expandedChildren == null && statistics.isDecided) {
			AnalysisResult<M> result = new AnalysisResult<>(statistics.player);
			result.addMoveWithScore(null, statistics.getMeanValue());
			return result;
		} else if (expandedChildren == null || expandedChildren.isEmpty()) {
			return null;
		}

		AnalysisResult<M> result = new AnalysisResult<>(statistics.player);
		int i = 0;
		while (i < expandedChildren.size()) {
			MonteCarloGameNode<M, P> childNode = expandedChildren.get(i++);
			double score = childNode.statistics.getMeanValue();
			result.addMoveWithScore(childNode.parentMove, statistics.player == childNode.statistics.player ? score : -score);
		}
		return result;
	}

	private void select() {
		double bestExpectedValue = LOSS;
		M move = null;
		MonteCarloGameNode<M, P> childNode = null;

		int i = 0;
		do {
			MonteCarloGameNode<M, P> child = expandedChildren.get(i);
			if (child.statistics.isDecided) {
				continue;
			}
			double meanValue = statistics.player == child.statistics.player ? child.statistics.getMeanValue() : -child.statistics.getMeanValue();
			double childExpectedValue = meanValue + child.statistics.getUncertainty(statistics.nodesEvaluated);
			if (move == null || childExpectedValue > bestExpectedValue) {
				bestExpectedValue = childExpectedValue;
				move = child.parentMove;
				childNode = child;
			}
		} while (++i < expandedChildren.size());

		position.makeMove(move);
		childNode.search();
		position.unmakeMove(move);
	}

	private void expand() {
		int moveIndex = RANDOM.nextInt(numUnexpanded);
		int moveListIndex = unexpandedIndexes[moveIndex];
		unexpandedIndexes[moveIndex] = unexpandedIndexes[--numUnexpanded];

		M move = moveList.get(moveListIndex);

		if (expandObserver != null) {
			expandObserver.notifyForked(move);
		}

		position.makeMove(move);
		MonteCarloGameNode<M, P> childNode = new MonteCarloGameNode<>(this, move, position, positionEvaluator, moveListFactory, numSimulations, expandObserver);
		expandedChildren.add(childNode);

		if (childNode.moveList.size() == 0) {
			childNode.backPropagate(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, childNode.moveList)), true);
		} else {
			childNode.simulate();
		}
		position.unmakeMove(move);
	}

	private void simulate() {
		MonteCarloStatistics result = new MonteCarloStatistics(statistics.player);

		MoveList<M> possibleMoves = moveListProdiver.getMoveList(0);

		int simulation = 0;
		do {
			List<M> movesMade = new ArrayList<>();

			possibleMoves.clear();
			position.getPossibleMoves(possibleMoves);

			while (possibleMoves.size() > 0) {
				M move = possibleMoves.get(RANDOM.nextInt(possibleMoves.size()));
				position.makeMove(move);
				movesMade.add(move);
				possibleMoves.clear();
				position.getPossibleMoves(possibleMoves);
			}

			result.addScore(statistics.player == position.getCurrentPlayer() ? positionEvaluator.evaluate(position, possibleMoves) : -positionEvaluator.evaluate(position, possibleMoves));

			int i = movesMade.size();
			while (--i >= 0) {
				position.unmakeMove(movesMade.get(i));
			}
		} while (!stopRequested && ++simulation < numSimulations);

		backPropagate(result, false);
	}

	private void backPropagate(MonteCarloStatistics result, boolean decided) {
		statistics.updateWith(result);
		if (decided) {
			statistics.setDecided();
		}
		MonteCarloGameNode<M, P> parent = parentNode;
		while (parent != null) {
			parent.updateStatistics();
			parent = parent.parentNode;
		}
	}

	private void updateStatistics() {
		boolean allChildrenDecided = numUnexpanded == 0;

		statistics.clear();

		int i = 0;
		do {
			MonteCarloGameNode<M, P> child = expandedChildren.get(i);
			statistics.updateWith(child.statistics);
			if (child.statistics.isWin(statistics.player)) {
				statistics.setDecided();
				return;
			}
			allChildrenDecided = allChildrenDecided && child.statistics.isDecided;
		} while (++i < expandedChildren.size());

		if (allChildrenDecided) {
			statistics.setDecided();
		}
	}

	public synchronized void stopSearch() {
		stopRequested = true;
		while (isSearching) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String toString() {
		return parentMove.toString();
	}
}
