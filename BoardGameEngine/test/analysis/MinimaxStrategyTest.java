package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import analysis.search.GameTreeSearch;
import analysis.search.MoveWithResult;
import game.IPosition;
import game.lock.TestLockingEvaluator;
import game.lock.TestLockingNode;
import game.lock.TestLockingPosition;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class MinimaxStrategyTest {
	private synchronized <M, P extends IPosition<M, P>> AnalysisResult<M> search(MinimaxStrategy<M, P> minimaxStrategy, P testGamePosition, int currentPlayer, int plies) {
		List<AnalysisResult<M>> result = new ArrayList<>();
		GameTreeSearch<M, P> gameTreeSearch = new GameTreeSearch<M, P>(null, testGamePosition, currentPlayer, plies, minimaxStrategy, moveWithResult -> {
			synchronized (this) {
				result.add(moveWithResult.result);
				notify();
			}
		});
		gameTreeSearch.search();
		while (result.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return result.get(0);
	}

	@Test
	public void testDepth_Zero() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 0);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertNull(result.getBestMove());
		assertEquals("null move: 0.0", result.toString());
	}

	@Test
	public void testDepth_One() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 1);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(2, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 1.0\n"
				+ "2 -> [4, 3]: 2.0", result.toString());
	}

	@Test
	public void testDepth_Two() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 2);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(1, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 5.0\n"
				+ "2 -> [4, 3]: 3.0", result.toString());
	}

	@Test
	public void testDepth_Three() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 3);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(2, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 8.0\n"
				+ "2 -> [4, 3]: 12.0", result.toString());
	}

	@Test
	public void testDepth_Four() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 4);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(1, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testDepth_Five() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, testGamePosition.getCurrentPlayer(), 5);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(1, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testStopSearch() throws InterruptedException {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> minimaxStrategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> result = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> gameTreeSearch = new GameTreeSearch<>(null, testLockingPosition, 0, 1, minimaxStrategy,
				moveWithResult -> result.add(moveWithResult.result));

		Thread thread = new Thread(() -> gameTreeSearch.search());
		thread.start();

		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			lockedNode.unlock();
		}).start();

		Thread.sleep(50);

		gameTreeSearch.stopSearch();

		thread.join();

		assertEquals(2, result.get(0).getMovesWithScore().size());
		assertEquals(1, result.get(0).getUnanalyzedMoves().size());
	}

	@Test
	public void testJoin_Results() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<MoveWithResult<TestGameNode>> results = new ArrayList<>();
		for (TestGameNode move : testGamePosition.getPossibleMoves()) {
			testGamePosition.makeMove(move);
			results.add(new MoveWithResult<>(move, search(minimaxStrategy, testGamePosition, 0, 3)));
			testGamePosition.unmakeMove(move);
		}
		AnalysisResult<TestGameNode> result = GameTreeSearch.join(minimaxStrategy, null, testGamePosition, 0, Collections.emptyList(), results).result;
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testJoin_MovesWithScore() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<MoveWithScore<TestGameNode>> movesWithScore = search(minimaxStrategy, testGamePosition, 0, 4).getMovesWithScore();
		AnalysisResult<TestGameNode> result = GameTreeSearch.join(minimaxStrategy, null, testGamePosition, 0, movesWithScore, Collections.emptyList()).result;
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testJoin_Mixed() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		List<MoveWithScore<TestGameNode>> firstMoveWithScore = Collections.singletonList(search(minimaxStrategy, testGamePosition, 0, 4).getMovesWithScore().get(0));

		List<MoveWithResult<TestGameNode>> results = new ArrayList<>();
		for (TestGameNode move : testGamePosition.getPossibleMoves()) {
			testGamePosition.makeMove(move);
			results.add(new MoveWithResult<>(move, search(minimaxStrategy, testGamePosition, 0, 3)));
			testGamePosition.unmakeMove(move);
		}

		List<MoveWithResult<TestGameNode>> secondResult = Collections.singletonList(results.get(1));
		AnalysisResult<TestGameNode> result = GameTreeSearch.join(minimaxStrategy, null, testGamePosition, 0, firstMoveWithScore, secondResult).result;
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}
}
