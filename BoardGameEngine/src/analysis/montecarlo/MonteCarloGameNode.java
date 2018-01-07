package analysis.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import game.forkjoinexample.ForkObserver;

public class MonteCarloGameNode<M, P extends IPosition<M>> {
	private static final Random RANDOM = new Random();

	private final MonteCarloGameNode<M, P> parentNode;

	private final P position;
	final M parentMove;
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListFactory<M> moveListFactory;
	private final int numSimulations;

	private final MoveList<M> moveList;
	private int numUnexpanded;
	int[] unexpandedIndexes;

	List<MonteCarloGameNode<M, P>> expandedChildren;

	final MonteCarloStatistics statistics;

	private final ForkObserver<M> expandObserver;

	private boolean isSearching = false;
	private volatile boolean stopRequested = false;

	public MonteCarloGameNode(MonteCarloGameNode<M, P> parentNode, M parentMove, P position, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSumulations) {
		this(parentNode, parentMove, position, positionEvaluator, moveListFactory, numSumulations, null);
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
		moveList = moveListFactory.newAnalysisMoveList();
		position.getPossibleMoves(moveList);
		numUnexpanded = moveList.size();
		statistics = new MonteCarloStatistics(position.getCurrentPlayer());
	}

	public void searchRoot() {
		isSearching = true;

		initUnexpanded();

		do {
			if (numUnexpanded == 0) {
				MonteCarloStatistics result = select();
				statistics.updateWith(result);
			} else {
				MonteCarloStatistics result = expand();
				statistics.updateWith(result);
			}
		} while (!stopRequested);

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

	private MonteCarloStatistics search() {
		if (expandedChildren == null) {
			if (parentNode.numUnexpanded == 0) {
				if (moveList.size() == 0) {
					MonteCarloStatistics result = new MonteCarloStatistics(position.getCurrentPlayer());
					result.addScore(positionEvaluator.evaluate(position, moveList));
					statistics.updateWith(result);
					return result;
				} else {
					initUnexpanded();
					MonteCarloStatistics result = expand();
					statistics.updateWith(result);
					return result;
				}
			} else {
				MonteCarloStatistics result = simulate();
				statistics.updateWith(result);
				return result;
			}
		} else if (numUnexpanded > 0) {
			MonteCarloStatistics result = expand();
			statistics.updateWith(result);
			return result;
		} else {
			MonteCarloStatistics result = select();
			statistics.updateWith(result);
			return result;
		}
	}

	public AnalysisResult<M> getResult() {
		AnalysisResult<M> result = new AnalysisResult<>(statistics.player);
		for (MonteCarloGameNode<M, P> childNode : expandedChildren) {
			double score = childNode.statistics.getExpectedValue(statistics.nodesEvaluated);
			result.addMoveWithScore(childNode.parentMove, statistics.player == childNode.statistics.player ? score : -score);
		}
		return result;
	}

	private MonteCarloStatistics select() {
		double bestExpectedValue = 0;
		M move = null;
		MonteCarloGameNode<M, P> childNode = null;

		int i = 0;
		do {
			MonteCarloGameNode<M, P> child = expandedChildren.get(i);
			double childExpectedValue = child.statistics.getExpectedValue(statistics.nodesEvaluated);
			if (move == null || childExpectedValue > bestExpectedValue) {
				bestExpectedValue = childExpectedValue;
				move = child.parentMove;
				childNode = child;
			}
		} while (++i < expandedChildren.size());

		position.makeMove(move);
		MonteCarloStatistics result = childNode.search();
		position.unmakeMove(move);

		return result;
	}

	private MonteCarloStatistics expand() {
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

		MonteCarloStatistics search = childNode.search();
		position.unmakeMove(move);

		return search;
	}

	private MonteCarloStatistics simulate() {
		MonteCarloStatistics result = new MonteCarloStatistics(statistics.player);

		MoveList<M> possibleMoves = moveListFactory.newAnalysisMoveList();

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

		result.nodesEvaluated = simulation;

		return result;
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
