package analysis.montecarlo;

import java.util.Collections;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import analysis.ITreeSearcher;
import analysis.PartialResultObservable;
import analysis.search.ThreadNumber;
import game.IPosition;
import game.MoveListFactory;
import game.forkjoinexample.ForkObserver;
import game.forkjoinexample.StartStopObserver;
import gui.analysis.ComputerPlayerResult;

public class MonteCarloTreeSearcher<M, P extends IPosition<M>> implements ITreeSearcher<M, P>, PartialResultObservable {
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListFactory<M> moveListFactory;

	private Thread treeSearchThread;
	private final ForkObserver<M> expandObserver;
	private StartStopObserver startStopObserver;

	private final int numSimulations;
	private MonteCarloGameNode<M, P> monteCarloNode;

	private volatile boolean searchComplete = false;

	private volatile AnalysisResult<M> result;

	public MonteCarloTreeSearcher(IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSimulations) {
		this(positionEvaluator, moveListFactory, numSimulations, null, null);
	}

	public MonteCarloTreeSearcher(IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSimulations, ForkObserver<M> expandObserver,
			StartStopObserver startStopObserver) {
		this.positionEvaluator = positionEvaluator;
		this.moveListFactory = moveListFactory;
		this.numSimulations = numSimulations;
		this.expandObserver = expandObserver;
		this.startStopObserver = startStopObserver;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void searchForever(P position, boolean escapeEarly) {
		searchComplete = false;
		treeSearchThread = new Thread(() -> startSearch((P) position.createCopy(), escapeEarly), "Monte_Carlo_Search_Thread_" + ThreadNumber.getThreadNum(getClass()));
		treeSearchThread.start();
	}

	private void startSearch(P position, boolean escapeEarly) {
		if (startStopObserver != null) {
			startStopObserver.notifyPlyStarted();
		}
		monteCarloNode = new MonteCarloGameNode<>(null, null, position, positionEvaluator, moveListFactory, numSimulations, expandObserver);
		monteCarloNode.searchRoot(escapeEarly);
		result = calculatePartialResult();
		searchComplete = true;
	}

	@Override
	public boolean isSearching() {
		return !searchComplete;
	}

	@Override
	public void stopSearch(boolean gameOver) {
		if (monteCarloNode != null) {
			monteCarloNode.stopSearch();
		}
		if (startStopObserver != null) {
			startStopObserver.notifyPlyComplete(true);
		}
		if (treeSearchThread != null) {
			try {
				treeSearchThread.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public AnalysisResult<M> getResult() {
		return result;
	}

	private AnalysisResult<M> calculatePartialResult() {
		if (monteCarloNode.expandedChildren == null && monteCarloNode.statistics.isDecided) {
			AnalysisResult<M> result = new AnalysisResult<>(monteCarloNode.statistics.player);
			result.addMoveWithScore(null, convertScore(monteCarloNode.statistics, true));
			return result;
		} else if (monteCarloNode.expandedChildren == null || monteCarloNode.expandedChildren.isEmpty()) {
			return null;
		}

		AnalysisResult<M> result = new AnalysisResult<>(monteCarloNode.statistics.player);
		int i = 0;
		while (i < monteCarloNode.expandedChildren.size()) {
			MonteCarloGameNode<M, P> childNode = monteCarloNode.expandedChildren.get(i++);
			result.addMoveWithScore(childNode.parentMove, convertScore(childNode.statistics, monteCarloNode.statistics.player == childNode.statistics.player));
		}
		return result;
	}

	private static double convertScore(MonteCarloStatistics statistics, boolean isCurrentPlayer) {
		double meanValue = isCurrentPlayer ? statistics.getMeanValue() : -statistics.getMeanValue();
		if (statistics.isDecided) {
			if (meanValue == MonteCarloStatistics.WIN) {
				return AnalysisResult.WIN;
			} else if (meanValue == MonteCarloStatistics.DRAW) {
				return AnalysisResult.DRAW;
			}
			return AnalysisResult.LOSS;
		}
		return meanValue;
	}

	public MonteCarloGameNode<M, P> getRoot() {
		return monteCarloNode;
	}

	@Override
	public void clearResult() {
		result = null;
		monteCarloNode = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ComputerPlayerResult getPartialResult() {
		if (monteCarloNode != null) {
			AnalysisResult<M> partialResult = result == null ? calculatePartialResult() : result;
			return new ComputerPlayerResult((AnalysisResult<Object>) partialResult, Collections.emptyMap(), monteCarloNode.statistics.nodesEvaluated);
		} else {
			return new ComputerPlayerResult(null, Collections.emptyMap(), 0);
		}
	}
}
