package analysis.montecarlo;

import java.util.Collections;
import java.util.List;

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
	private final IMonteCarloChildren<M> monteCarloChildren;
	private final IPositionEvaluator<M, P> positionEvaluator;
	private final MoveListFactory<M> moveListFactory;

	private Thread treeSearchThread;
	private final ForkObserver<M> expandObserver;
	private StartStopObserver startStopObserver;

	private final int numSimulations;
	private final int maxDepth;
	private MonteCarloGameNode<M, P> monteCarloNode;

	private volatile boolean searchComplete = false;

	private volatile AnalysisResult<M> result;

	public MonteCarloTreeSearcher(IMonteCarloChildren<M> monteCarloChildren, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSimulations, int maxDepth) {
		this(monteCarloChildren, positionEvaluator, moveListFactory, numSimulations, maxDepth, null, null);
	}

	public MonteCarloTreeSearcher(IMonteCarloChildren<M> monteCarloChildren, IPositionEvaluator<M, P> positionEvaluator, MoveListFactory<M> moveListFactory, int numSimulations, int maxDepth,
			ForkObserver<M> expandObserver, StartStopObserver startStopObserver) {
		this.monteCarloChildren = monteCarloChildren;
		this.positionEvaluator = positionEvaluator;
		this.moveListFactory = moveListFactory;
		this.numSimulations = numSimulations;
		this.maxDepth = maxDepth;
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
		result = null;
		monteCarloNode = new MonteCarloGameNode<>(null, null, position, monteCarloChildren, positionEvaluator, moveListFactory, numSimulations, maxDepth, expandObserver);
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
		List<MonteCarloGameNode<M, P>> expandedChildren = monteCarloNode.expandedChildren;
		if (expandedChildren == null && monteCarloNode.statistics.isDecided) {
			AnalysisResult<M> result = new AnalysisResult<>(monteCarloNode.statistics.player);
			result.addMoveWithScore(null, convertScore(monteCarloNode.statistics, true, monteCarloNode.statistics.numUncertain));
			return result;
		} else if (expandedChildren == null || expandedChildren.isEmpty()) {
			return null;
		}

		AnalysisResult<M> result = new AnalysisResult<>(monteCarloNode.statistics.player);
		int i = 0;
		while (i < expandedChildren.size()) {
			MonteCarloGameNode<M, P> childNode = expandedChildren.get(i++);
			result.addMoveWithScore(childNode.parentMove,
					convertScore(childNode.statistics, monteCarloNode.statistics.player == childNode.statistics.player, monteCarloNode.statistics.numUncertain));
		}
		return result;
	}

	private static double convertScore(MonteCarloStatistics statistics, boolean isCurrentPlayer, int parentNumUncertain) {
		double meanValue = isCurrentPlayer ? statistics.getMeanValue() : -statistics.getMeanValue();
		if (statistics.isDecided) {
			if (meanValue == MonteCarloStatistics.WIN) {
				return AnalysisResult.WIN;
			} else if (meanValue == MonteCarloStatistics.LOSS) {
				return AnalysisResult.LOSS;
			}
			return AnalysisResult.DRAW;
		}
		return meanValue - statistics.getUncertainty(parentNumUncertain) / 2;
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
			return new ComputerPlayerResult((AnalysisResult<Object>) partialResult, Collections.emptyMap(), monteCarloNode.statistics.getTotalNodesEvaluated());
		} else {
			return new ComputerPlayerResult(null, Collections.emptyMap(), 0);
		}
	}
}
