package analysis.search;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MinimaxStrategy;
import analysis.MoveWithScore;
import game.ultimatetictactoe.UTTTCoordinate;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class IterativeDeepeningTreeSearcherTest {
	private void doTest(int numThreads) throws InterruptedException {
		MinimaxStrategy<UTTTCoordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator());
		IterativeDeepeningTreeSearcher<UTTTCoordinate, UltimateTicTacToePosition> iterativeDeepeningStrategy = new IterativeDeepeningTreeSearcher<>(minimaxStrategy, numThreads);
		iterativeDeepeningStrategy.searchForever(new UltimateTicTacToePosition());
		Thread.sleep(50);
		System.out.println("Stopping " + numThreads + "... ");
		iterativeDeepeningStrategy.stopSearch(true);
		AnalysisResult<UTTTCoordinate> result = iterativeDeepeningStrategy.getResult();
		ArrayList<MoveWithScore<UTTTCoordinate>> movesWithScore = new ArrayList<>(result.getMovesWithScore());
		Collections.sort(movesWithScore, (a, b) -> -Double.compare(a.score, b.score));
		for (int i = 0; i < 3; ++i) {
			System.out.println(movesWithScore.get(i));
		}
		System.out.println("plies: " + iterativeDeepeningStrategy.getPlies());
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

	@Test
	public void testCompareSpeeds() throws InterruptedException {
		doSpeedTest(1);
		doSpeedTest(2);
		doSpeedTest(3);
		doSpeedTest(4);
		doSpeedTest(5);
		doSpeedTest(6);
	}

	private void doSpeedTest(int numThreads) {
		int numPlies = 6;
		MinimaxStrategy<UTTTCoordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator());
		IterativeDeepeningTreeSearcher<UTTTCoordinate, UltimateTicTacToePosition> iterativeDeepeningStrategy = new IterativeDeepeningTreeSearcher<>(minimaxStrategy, numThreads);
		long start = System.currentTimeMillis();
		iterativeDeepeningStrategy.startSearch(new UltimateTicTacToePosition(), numPlies);
		System.out.println(numThreads + " workers " + numPlies + " plies in " + (System.currentTimeMillis() - start) + "ms");
	}
}
