package game.ultimatetictactoe;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import analysis.ComputerPlayer;
import analysis.strategy.MinimaxStrategy;

public class UTTTComputerPlayerTest {
	@Test
	@Ignore
	public void testStopOnTime_OneWorker() {
		testStopOnTime(1, 2000);
	}

	@Test
	@Ignore
	public void testStopOnTime_TwoWorker() {
		testStopOnTime(2, 2000);
	}

	@Test
	public void testStopOnTime_ThreeWorker() {
		testStopOnTime(3, 2000);
	}

	@Test
	public void testMakeTwoMoves() {
		ComputerPlayer player = newComputerPlayer(2, 50);
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		player.getMove(position);
		player.getMove(position);
	}

	private void testStopOnTime(int numWorkers, long toWait) {
		ComputerPlayer player = newComputerPlayer(numWorkers, toWait);
		long start = System.currentTimeMillis();
		long extraTime = 1000;
		long allottedTime = toWait + extraTime;
		player.getMove(new UltimateTicTacToePosition());
		long timeTaken = System.currentTimeMillis() - start;
		System.out.println("Stopped " + numWorkers + " workers in " + (timeTaken - toWait) + "ms");
		assertTrue(Long.toString(timeTaken - allottedTime) + "ms over", timeTaken < allottedTime);
	}

	private static ComputerPlayer newComputerPlayer(int numWorkers, long toWait) {
		return new ComputerPlayer(new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator()), numWorkers, "Computer", toWait);
	}
}
