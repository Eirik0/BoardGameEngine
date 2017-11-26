package analysis.search;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.strategy.MinimaxStrategy;
import game.Coordinate;
import game.MoveListFactory;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class IterativeDeepeningTreeSearcherTest {
	private void doTest(int numThreads) throws InterruptedException {
		MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
		MinimaxStrategy<Coordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());
		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> iterativeDeepeningStrategy = new IterativeDeepeningTreeSearcher<>(minimaxStrategy,
				moveListFactory, numThreads);

		iterativeDeepeningStrategy.searchForever(new UltimateTicTacToePosition());
		Thread.sleep(50);
		iterativeDeepeningStrategy.stopSearch(true);
		AnalysisResult<Coordinate> result = iterativeDeepeningStrategy.getResult();
		System.out.println(numThreads + " workers, plies: " + iterativeDeepeningStrategy.getPlies() + ", bestMove = " + result.getBestMove() + ": " + result.getMax());
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
		MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
		MinimaxStrategy<Coordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());
		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> iterativeDeepeningSearcher = new IterativeDeepeningTreeSearcher<>(minimaxStrategy,
				moveListFactory, numThreads);

		long start = System.currentTimeMillis();
		iterativeDeepeningSearcher.startSearch(new UltimateTicTacToePosition(), numPlies);
		System.out.println(numThreads + " workers " + numPlies + " plies in " + (System.currentTimeMillis() - start) + "ms");
		iterativeDeepeningSearcher.stopSearch(true);
	}
}
