package game.ultimatetictactoe;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import analysis.ComputerPlayer;

public class UTTTComputerPlayerTest {
	@Test
	public void testStopOnTime_OneWorker() {
		testStopOnTime(1, 2000);
	}

	@Test
	public void testStopOnTime_TwoWorker() {
		testStopOnTime(2, 20000);
	}

	private void testStopOnTime(int numWorkers, long toWait) {
		ComputerPlayer player = UltimateTicTacToeGame.newComputerPlayer(numWorkers, toWait);
		long start = System.currentTimeMillis();
		player.getMove(new UltimateTicTacToePosition());
		long timeTaken = System.currentTimeMillis() - start;
		long extraTime = 1000;
		long allottedTime = toWait + extraTime;
		assertTrue(Long.toString(timeTaken - allottedTime) + " over", timeTaken < allottedTime);
	}
}
