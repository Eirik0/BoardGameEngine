package analysis.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.search.GameTreeSearch;
import game.ArrayMoveList;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import game.lock.TestLockingEvaluator;
import game.lock.TestLockingNode;
import game.lock.TestLockingPosition;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class MinimaxStrategyTest {
	private synchronized <M, P extends IPosition<M, P>> AnalysisResult<M> search(MinimaxStrategy<M, P> minimaxStrategy, P testGamePosition, int plies) {
		List<AnalysisResult<M>> result = new ArrayList<>();
		MoveListFactory<M> moveListFactory = new MoveListFactory<>(2);
		MoveList<M> moveList = moveListFactory.newAnalysisMoveList();
		testGamePosition.getPossibleMoves(moveList);
		GameTreeSearch<M, P> gameTreeSearch = new GameTreeSearch<>(null, testGamePosition, moveList, moveListFactory, plies, minimaxStrategy, (canceled, player, moveWithResult) -> {
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
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 0);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertNull(result.getBestMove());
		assertEquals("null move: 0.0", result.toString());
	}

	@Test
	public void testDepth_One() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 1);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertEquals(-2, result.getBestMove().getValue());
		assertEquals("-1 -> [6, 5]: 1.0\n"
				+ "-2 -> [4, 3]: 2.0", result.toString());
	}

	@Test
	public void testDepth_Two() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 2);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertEquals(-1, result.getBestMove().getValue());
		assertEquals("-1 -> [6, 5]: 5.0\n"
				+ "-2 -> [4, 3]: 3.0", result.toString());
	}

	@Test
	public void testDepth_Three() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 3);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertEquals(-2, result.getBestMove().getValue());
		assertEquals("-1 -> [6, 5]: 8.0\n"
				+ "-2 -> [4, 3]: 12.0", result.toString());
	}

	@Test
	public void testDepth_Four() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 4);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertEquals(-1, result.getBestMove().getValue());
		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testDepth_Five() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 5);

		assertEquals("0 -> [-1, -2]", testGamePosition.toString());
		assertEquals(-1, result.getBestMove().getValue());
		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", result.toString());
	}

	@Test
	public void testStopSearch() throws InterruptedException {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		MoveListFactory<TestLockingNode> moveListFactory = new MoveListFactory<>(3);
		MoveList<TestLockingNode> moveList = moveListFactory.newAnalysisMoveList();
		testLockingPosition.getPossibleMoves(moveList);
		List<AnalysisResult<TestLockingNode>> result = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> gameTreeSearch = new GameTreeSearch<>(null, testLockingPosition, moveList, moveListFactory, 1,
				new MinimaxStrategy<>(moveListFactory, new TestLockingEvaluator()), (canceled, player, moveWithResult) -> result.add(moveWithResult.result));

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
		assertEquals(1, gameTreeSearch.getRemainingBranches());
	}

	@Test
	public void testJoin_Results() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());
		Map<TestGameNode, AnalysisResult<TestGameNode>> results = new HashMap<>();
		MoveList<TestGameNode> possibleMoves = new ArrayMoveList<>(2);
		testGamePosition.getPossibleMoves(possibleMoves);
		int i = 0;
		while (i < possibleMoves.size()) {
			TestGameNode move = possibleMoves.get(i);
			testGamePosition.makeMove(move);
			results.put(move, search(minimaxStrategy, testGamePosition, 3));
			testGamePosition.unmakeMove(move);
			++i;
		}
		AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>();
		minimaxStrategy.join(testGamePosition, 0, 1, partialResult, results);
		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", partialResult.toString());
	}

	@Test
	public void testJoin_MovesWithScore() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());
		List<MoveWithScore<TestGameNode>> movesWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore();
		AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>();
		for (MoveWithScore<TestGameNode> moveWithScore : movesWithScore) {
			partialResult.addMoveWithScore(moveWithScore.move, moveWithScore.score);
		}
		minimaxStrategy.join(testGamePosition, 0, 0, partialResult, Collections.emptyMap());
		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", partialResult.toString());
	}

	@Test
	public void testJoin_Mixed() {
		TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new MoveListFactory<>(2), new TestGameEvaluator());

		MoveWithScore<TestGameNode> moveWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore().get(0);
		AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(moveWithScore.move, moveWithScore.score);
		MoveList<TestGameNode> possibleMoves = new ArrayMoveList<>(2);
		testGamePosition.getPossibleMoves(possibleMoves);
		int i = 0;
		Map<TestGameNode, AnalysisResult<TestGameNode>> results = new HashMap<>();
		while (i < possibleMoves.size()) {
			TestGameNode move = possibleMoves.get(i);
			testGamePosition.makeMove(move);
			results.put(move, search(minimaxStrategy, testGamePosition, 3));
			testGamePosition.unmakeMove(move);
			++i;
		}

		Entry<TestGameNode, AnalysisResult<TestGameNode>> firstEntry = results.entrySet().iterator().next();
		Map<TestGameNode, AnalysisResult<TestGameNode>> secondResult = Collections.singletonMap(firstEntry.getKey(), firstEntry.getValue());
		minimaxStrategy.join(testGamePosition, 0, 1, partialResult, secondResult);
		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", partialResult.toString());
	}
}
