package analysis.montecarlo;

import java.util.ArrayList;
import java.util.List;

import analysis.IPositionEvaluator;
import analysis.strategy.MoveListProvider;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import game.forkjoinexample.ForkObserver;

public class MonteCarloGameNode<M, P extends IPosition<M>> {
	private MonteCarloGameNode<M, P> parentNode;

	final M parentMove;
	private final P position;
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListFactory<M> moveListFactory;
	private MoveListProvider<M> moveListProdiver;
	private final int numSimulations;

	private final MoveList<M> moveList;

	final MonteCarloChildren<M> unexpandedChildren;
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
		unexpandedChildren = new MonteCarloChildren<>(moveList);
		statistics = new MonteCarloStatistics(position.getCurrentPlayer());
	}

	public void searchRoot(boolean escapeEarly) {
		isSearching = true;
		if (moveList.size() == 0) {
			statistics.updateWith(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, moveList)));
			statistics.setDecided();
		} else {
			expandedChildren = new ArrayList<>(moveList.size());
			unexpandedChildren.initUnexpanded();
			do {
				search();
				if (escapeEarly && statistics.isDecided && statistics.numWon > 0) {
					break;
				} else if (statistics.isDecided) {
					boolean allDecided = true;
					int i = 0;
					do {
						allDecided = allDecided && expandedChildren.get(i++).statistics.isDecided;
					} while (allDecided && i < expandedChildren.size());
					if (allDecided) {
						break;
					}
				}
			} while (!stopRequested);
		}
		synchronized (this) {
			isSearching = false;
			notify();
		}
	}

	private void search() {
		MonteCarloGameNode<M, P> nodeToExpand = this;
		while (nodeToExpand.unexpandedChildren.numUnexpanded == 0) {
			nodeToExpand = nodeToExpand.select();
		}
		MonteCarloGameNode<M, P> nodeToSimulate = nodeToExpand.expand();
		if (nodeToSimulate.moveList.size() == 0) {
			nodeToSimulate.backPropagate(new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, nodeToSimulate.moveList)), true);
		} else {
			MonteCarloStatistics result = nodeToSimulate.simulate();
			nodeToSimulate.backPropagate(result, result.isDecided);
		}
	}

	private MonteCarloGameNode<M, P> select() {
		MonteCarloGameNode<M, P> bestChild = null;
		double bestExpectedValue = MonteCarloStatistics.LOSS;
		int i = 0;
		do {
			MonteCarloGameNode<M, P> child = expandedChildren.get(i);
			if (child.statistics.isDecided) {
				continue;
			}
			double meanValue = statistics.player == child.statistics.player ? child.statistics.getMeanValue() : -child.statistics.getMeanValue();
			double childExpectedValue = meanValue + child.statistics.getUncertainty(statistics.nodesEvaluated);
			if (bestChild == null || childExpectedValue > bestExpectedValue) {
				bestExpectedValue = childExpectedValue;
				bestChild = child;
			}
		} while (++i < expandedChildren.size());
		position.makeMove(bestChild.parentMove);
		return bestChild;
	}

	private MonteCarloGameNode<M, P> expand() {
		if (expandedChildren == null) {
			expandedChildren = new ArrayList<>(moveList.size());
			unexpandedChildren.initUnexpanded();
		}

		M move = moveList.get(unexpandedChildren.getNextMoveIndex());

		if (expandObserver != null) {
			expandObserver.notifyForked(move);
		}

		position.makeMove(move);
		MonteCarloGameNode<M, P> childNode = new MonteCarloGameNode<>(this, move, position, positionEvaluator, moveListFactory, numSimulations, expandObserver);
		expandedChildren.add(childNode);
		return childNode;
	}

	private MonteCarloStatistics simulate() {
		MonteCarloStatistics result = new MonteCarloStatistics(statistics.player);

		MoveList<M> possibleMoves = moveListProdiver.getMoveList(0);
		position.getPossibleMoves(possibleMoves);
		if (possibleMoves.size() == 0) {
			return new MonteCarloStatistics(position.getCurrentPlayer(), positionEvaluator.evaluate(position, possibleMoves));
		}

		int simulation = 0;
		List<M> movesMade = new ArrayList<>();
		do {
			while (possibleMoves.size() > 0) {
				M move = possibleMoves.get(unexpandedChildren.getNextMoveIndex(possibleMoves));
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

			movesMade.clear();
			possibleMoves.clear();
			position.getPossibleMoves(possibleMoves);
		} while (!stopRequested && ++simulation < numSimulations);

		return result;
	}

	private void backPropagate(MonteCarloStatistics result, boolean decided) {
		statistics.updateWith(result);
		if (decided) {
			statistics.setDecided();
		}
		position.unmakeMove(parentMove);
		MonteCarloGameNode<M, P> parent = parentNode;
		while (parent != null) {
			parent.updateStatistics();
			if (parent.parentMove != null) {
				position.unmakeMove(parent.parentMove);
			}
			parent = parent.parentNode;
		}
	}

	private void updateStatistics() {
		boolean allChildrenDecided = unexpandedChildren.numUnexpanded == 0;
		boolean won = false;
		statistics.clear();

		int i = 0;
		do {
			MonteCarloGameNode<M, P> child = expandedChildren.get(i);
			statistics.updateWith(child.statistics);
			won = won || child.statistics.isWin(statistics.player);
			allChildrenDecided = allChildrenDecided && child.statistics.isDecided;
		} while (++i < expandedChildren.size());

		if (allChildrenDecided || won) {
			setDecided();
		}
	}

	private void setDecided() {
		statistics.setDecided();
		if (parentNode != null) {
			expandedChildren = null;
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
