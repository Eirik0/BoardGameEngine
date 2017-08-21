package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import game.lock.TestLockingEvaluator;
import game.lock.TestLockingNode;
import game.lock.TestLockingPosition;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import util.Pair;

public class MinimaxStrategyTest {

	@Test
	public void testDepth_Zero() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 0);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertNull(result.getBestMove());
		assertEquals("", result.toString());
	}

	@Test
	public void testDepth_One() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 1);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(2, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 1.0\n"
				+ "2 -> [4, 3]: 2.0", result.toString());
	}

	@Test
	public void testDepth_Two() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 2);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(1, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 5.0\n"
				+ "2 -> [4, 3]: 3.0", result.toString());
	}

	@Test
	public void testDepth_Three() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 3);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(2, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 8.0\n"
				+ "2 -> [4, 3]: 12.0", result.toString());
	}

	@Test
	public void testDepth_Four() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 4);

		assertEquals("0 -> [1, 2]", testGamePosition.toString());
		assertEquals(1, result.getBestMove().getValue());
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testDepth_Five() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = minimaxStrategy.search(testGamePosition, testGamePosition.getCurrentPlayer(), 5);

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
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();

		Thread thread = new Thread(() -> results.add(minimaxStrategy.search(testLockingPosition, 0, 1)));
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

		minimaxStrategy.stopSearch();

		thread.join();

		assertEquals(2, results.get(0).getMovesWithScore().size());
		assertEquals(1, results.get(0).getUnanalyzedMoves().size());
	}

	@Test
	public void testJoin_Results() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<Pair<TestGameNode, AnalysisResult<TestGameNode>>> results = new ArrayList<>();
		for (TestGameNode move : testGamePosition.getPossibleMoves()) {
			testGamePosition.makeMove(move);
			results.add(Pair.valueOf(move, minimaxStrategy.search(testGamePosition, 0, 3)));
			testGamePosition.unmakeMove(move);
		}
		AnalysisResult<TestGameNode> result = minimaxStrategy.join(testGamePosition, 0, Collections.emptyList(), results);
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testJoin_MovesWithScore() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<MoveWithScore<TestGameNode>> movesWithScore = minimaxStrategy.search(testGamePosition, 0, 4).getMovesWithScore();
		AnalysisResult<TestGameNode> result = minimaxStrategy.join(testGamePosition, 0, movesWithScore, Collections.emptyList());
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testJoin_Mixed() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator());

		List<MoveWithScore<TestGameNode>> firstMoveWithScore = Collections.singletonList(minimaxStrategy.search(testGamePosition, 0, 4).getMovesWithScore().get(0));

		List<Pair<TestGameNode, AnalysisResult<TestGameNode>>> results = new ArrayList<>();
		for (TestGameNode move : testGamePosition.getPossibleMoves()) {
			testGamePosition.makeMove(move);
			results.add(Pair.valueOf(move, minimaxStrategy.search(testGamePosition, 0, 3)));
			testGamePosition.unmakeMove(move);
		}

		List<Pair<TestGameNode, AnalysisResult<TestGameNode>>> secondResult = Collections.singletonList(results.get(1));

		AnalysisResult<TestGameNode> result = minimaxStrategy.join(testGamePosition, 0, firstMoveWithScore, secondResult);
		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", result.toString());
	}
}
