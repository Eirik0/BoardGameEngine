package analysis.montecarlo;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import analysis.ITreeSearcher;
import analysis.search.ThreadNumber;
import game.IPosition;
import game.MoveListFactory;
import game.forkjoinexample.ForkObserver;
import game.forkjoinexample.StartStopObserver;

public class MonteCarloTreeSearcher<M, P extends IPosition<M>> implements ITreeSearcher<M, P> {
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
		treeSearchThread = new Thread(() -> startSearch((P) position.createCopy()), "Monte_Carlo_Search_Thread_" + ThreadNumber.getThreadNum(getClass()));
		treeSearchThread.start();
	}

	private void startSearch(P position) {
		if (startStopObserver != null) {
			startStopObserver.notifyPlyStarted();
		}
		monteCarloNode = new MonteCarloGameNode<>(null, null, position, positionEvaluator, moveListFactory, numSimulations, expandObserver);
		monteCarloNode.searchRoot();
		result = monteCarloNode.getResult();
		searchComplete = true;
	}

	@Override
	public boolean isSearching() {
		return !searchComplete;
	}

	@Override
	public void stopSearch(boolean gameOver) {
		monteCarloNode.stopSearch();
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

	public MonteCarloGameNode<M, P> getRoot() {
		return monteCarloNode;
	}

	@Override
	public void clearResult() {
		result = null;
	}
}
