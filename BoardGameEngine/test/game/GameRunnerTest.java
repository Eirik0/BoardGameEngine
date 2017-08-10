package game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class GameRunnerTest {
	@Test
	public void testStartStopGame() throws InterruptedException {
		AddToListTestGame game = new AddToListTestGame();
		GameRunner<?, ?> gameRunner = new GameRunner<>(game);
		gameRunner.startNewGame(Collections.singletonList(game.player));
		Thread.sleep(10);// sleep a little to let the list populate
		gameRunner.endGame();
		assertTrue(game.list.size() > 0);
		assertEquals(2, game.numNewPositions);
		System.out.println(game.list.get(game.list.size() - 1));
	}

	@Test
	public void testStartTwice() throws InterruptedException {
		AddToListTestGame game = new AddToListTestGame();
		GameRunner<?, ?> gameRunner = new GameRunner<>(game);
		gameRunner.startNewGame(Collections.singletonList(game.player));
		gameRunner.startNewGame(Collections.singletonList(game.player));
		Thread.sleep(10);// sleep a little to let the list populate
		gameRunner.endGame();
		assertTrue(game.list.size() > 0);
		assertEquals(3, game.numNewPositions);
		System.out.println(game.list.get(game.list.size() - 1));
	}

	@Test
	public void testEndTwice() throws InterruptedException {
		AddToListTestGame game = new AddToListTestGame();
		GameRunner<?, ?> gameRunner = new GameRunner<>(game);
		gameRunner.startNewGame(Collections.singletonList(game.player));
		Thread.sleep(10);// sleep a little to let the list populate
		gameRunner.endGame();
		gameRunner.endGame();
		assertTrue(game.list.size() > 0);
		assertEquals(2, game.numNewPositions);
		System.out.println(game.list.get(game.list.size() - 1));
	}

	static class AddToListTestGame implements IGame<Integer, AddToListPosition> {
		final AddToListTestPlayer player = new AddToListTestPlayer();
		final List<Integer> list = new ArrayList<>();
		int numNewPositions = 0;

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public int getNumberOfPlayers() {
			return 1;
		}

		@Override
		public IPlayer[] getAvailablePlayers() {
			return new IPlayer[] { player };
		}

		@Override
		public IPlayer getDefaultPlayer() {
			return new AddToListTestPlayer();
		}

		@Override
		public AddToListPosition newInitialPosition() {
			numNewPositions++;
			list.clear();
			return new AddToListPosition(list);
		}
	}

	static class AddToListTestPlayer implements IPlayer {
		@Override
		public <M, P extends IPosition<M, P>> M getMove(IPosition<M, P> position) {
			return position.getPossibleMoves().get(0);
		}
	}

	static class AddToListPosition implements IPosition<Integer, AddToListPosition> {
		int index;
		final List<Integer> list;

		private AddToListPosition(List<Integer> list) {
			this(0, list);
		}

		private AddToListPosition(int index, List<Integer> list) {
			this.index = index;
			this.list = list;
		}

		@Override
		public List<Integer> getPossibleMoves() {
			return Collections.singletonList(index);
		}

		@Override
		public int getCurrentPlayer() {
			return 1;
		}

		@Override
		public void makeMove(Integer move) {
			list.add(move);
			++index;
		}

		@Override
		public void unmakeMove(Integer move) {
			list.remove(move);
			--index;
		}

		@Override
		public AddToListPosition createCopy() {
			return new AddToListPosition(index, new ArrayList<>(list));
		}
	}
}
