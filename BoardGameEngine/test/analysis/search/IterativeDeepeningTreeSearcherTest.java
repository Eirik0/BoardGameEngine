package analysis.search;

import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

import org.junit.Test;

import analysis.MinimaxStrategy;

public class IterativeDeepeningTreeSearcherTest {
	private void doTest(int numThreads) throws InterruptedException {
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<TestGameNode, TestGamePosition>(new TestGameEvaluator());
		IterativeDeepeningTreeSearcher<TestGameNode, TestGamePosition> iterativeDeepeningStrategy = new IterativeDeepeningTreeSearcher<>(minimaxStrategy, numThreads);
		iterativeDeepeningStrategy.searchForever(TestGamePosition.createTestPosition());
		Thread.sleep(50);
		System.out.println("Stopping " + numThreads + ": ");
		iterativeDeepeningStrategy.stopSearch();
		System.out.println(iterativeDeepeningStrategy.getResult().toString());
		System.out.println(iterativeDeepeningStrategy.getPlies());
	}

	@Test
	public void testStartStop_1() throws InterruptedException {
		doTest(1);
	}

	@Test
	public void testStartStop_2() throws InterruptedException {
		doTest(2);
	}

	@Test
	public void testStartStop_3() throws InterruptedException {
		doTest(3);
	}

	@Test
	public void testStartStop_4() throws InterruptedException {
		doTest(4);
	}

	@Test
	public void testStartStop_5() throws InterruptedException {
		doTest(5);
	}

	@Test
	public void testStartStop_6() throws InterruptedException {
		doTest(6);
	}
}
