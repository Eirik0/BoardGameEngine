package analysis.search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.strategy.MinimaxStrategy;
import game.MoveList;
import game.MoveListFactory;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class TreeSearchWorkerTest {
	@Test
	public void testJoinThread() {
		TreeSearchWorker worker = new TreeSearchWorker("test", finishedWorker -> {
		});
		worker.joinThread();
	}

	@Test
	public void testDoWork() throws InterruptedException {
		BlockingQueue<AnalysisResult<TestGameNode>> resultQueue = new SynchronousQueue<>();
		TreeSearchWorker worker = new TreeSearchWorker("test", finishedWorker -> {
		});
		MoveListFactory<TestGameNode> moveListFactory = new MoveListFactory<>(2);
		TestGamePosition position = TestGamePosition.createTestPosition();
		MoveList<TestGameNode> moveList = moveListFactory.newAnalysisMoveList();
		position.getPossibleMoves(moveList);
		worker.workOn(new GameTreeSearch<>(null, position, moveList, moveListFactory, 0, new MinimaxStrategy<>(moveListFactory, new TestGameEvaluator()), (canceled, player, moveWithResult) -> {
			try {
				resultQueue.put(moveWithResult.result);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
		resultQueue.take();
		worker.joinThread();
	}

	@Test
	public void testRescheduleWorkOnComplete() throws InterruptedException {
		List<AnalysisResult<TestGameNode>> resultList = new ArrayList<>();
		MoveListFactory<TestGameNode> moveListFactory = new MoveListFactory<>(2);
		TestGamePosition position = TestGamePosition.createTestPosition();
		MoveList<TestGameNode> moveList = moveListFactory.newAnalysisMoveList();
		position.getPossibleMoves(moveList);
		GameTreeSearch<TestGameNode, TestGamePosition> treeSearch = new GameTreeSearch<>(null, position, moveList, moveListFactory, 0,
				new MinimaxStrategy<>(moveListFactory, new TestGameEvaluator()), (canceled, player, moveWithResult) -> {
					synchronized (this) {
						resultList.add(moveWithResult.result);
						notify();
					}
				});
		TreeSearchWorker worker = new TreeSearchWorker("test", finishedWorker -> {
			synchronized (this) {
				if (resultList.size() < 2) {
					finishedWorker.workOn(treeSearch);
				}
			}
		});
		worker.workOn(treeSearch);
		synchronized (this) {
			while (resultList.size() < 2) {
				wait();
			}
		}
		worker.joinThread();
	}
}
