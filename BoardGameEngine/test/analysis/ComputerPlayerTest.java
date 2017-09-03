package analysis;

import java.util.Arrays;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import game.Coordinate;
import game.GameRunner;
import game.tictactoe.TicTacToeGame;
import game.tictactoe.TicTacToePosition;

public class ComputerPlayerTest {
	@Test
	@Ignore
	public void testDoNotWaitForAMoveIfFInishedSearching() throws InterruptedException {
		TicTacToeGame game = new TicTacToeGame();
		GameRunner<Coordinate, TicTacToePosition> gameRunner = new GameRunner<Coordinate, TicTacToePosition>(game);
		for (int i = 0; i < 100; ++i) {
			gameRunner.startNewGame(Arrays.asList(game.getAvailablePlayers()[1], game.getAvailablePlayers()[1]));
			int sleep = new Random().nextInt(1000); // To reset the game randomly
			System.out.println(i + 1 + ": " + sleep);
			Thread.sleep(sleep);
		}
	}
}
