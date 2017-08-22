package analysis.search;

import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MinimaxStrategy;

public class TreeSearchWorkerTest {
	@Test
	public void testJoinThread() {
		TreeSearchWorker<TestGameNode, TestGamePosition> worker = new TreeSearchWorker<>("test", finishedWorker -> {
		});
		worker.joinThread();
	}

	@Test
	public void testDoWork() throws InterruptedException {
		BlockingQueue<AnalysisResult<TestGameNode>> resultQueue = new SynchronousQueue<>();
		TreeSearchWorker<TestGameNode, TestGamePosition> worker = new TreeSearchWorker<>("test", finishedWorker -> {
		});
		worker.workOn(new GameTreeSearch<>(null, TestGamePosition.createTestPosition(), 0, 0, new MinimaxStrategy<>(new TestGameEvaluator()), result -> {
			try {
				resultQueue.put(result.getSecond());
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
		GameTreeSearch<TestGameNode, TestGamePosition> treeSearch = new GameTreeSearch<>(null, TestGamePosition.createTestPosition(), 0, 0, new MinimaxStrategy<>(new TestGameEvaluator()), result -> {
			resultList.add(result.getSecond());
		});

		TreeSearchWorker<TestGameNode, TestGamePosition> worker = new TreeSearchWorker<>("test", finishedWorker -> {
			if (resultList.size() < 2) {
				// add a new worker if there is more to be done
				finishedWorker.workOn(treeSearch);
			}
		});

		worker.workOn(treeSearch);

		synchronized (this) {
			while (resultList.size() < 2) {
				wait(10);
			}
		}

		worker.joinThread();
	}
}
