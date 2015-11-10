package analysis.search;

import org.junit.Test;

import analysis.MinimaxStrategy;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class IterativeDeepeningTreeSearcherTest {
	private void doTest(int numThreads) throws InterruptedException {
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<TestGameNode, TestGamePosition>(new TestGameEvaluator());
		IterativeDeepeningTreeSearcher<TestGameNode, TestGamePosition> iterativeDeepeningStrategy = new IterativeDeepeningTreeSearcher<>(minimaxStrategy, numThreads);
		iterativeDeepeningStrategy.searchForever(TestGamePosition.createTestPosition());
		Thread.sleep(50);
		iterativeDeepeningStrategy.stopSearch();
		System.out.println(numThreads + ": ");
		System.out.println(iterativeDeepeningStrategy.getResult().toString());
		System.out.println(iterativeDeepeningStrategy.getPlies());
	}

	@Test
	public void testStartStop() throws InterruptedException {
		doTest(1);
		doTest(2);
		doTest(3);
		doTest(4);
		doTest(5);
		doTest(6);
	}
}
