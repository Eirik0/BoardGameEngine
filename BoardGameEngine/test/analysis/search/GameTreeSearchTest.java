package analysis.search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MinimaxStrategy;
import game.lock.TestLockingEvaluator;
import game.lock.TestLockingNode;
import game.lock.TestLockingPosition;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class GameTreeSearchTest {
	@Test
	public void testSearch() {
		TestGamePosition position = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> strategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> treeSearch = new GameTreeSearch<TestGameNode, TestGamePosition>(null, position, 0, 1, strategy,
				moveResult -> results.add(moveResult.getSecond()));
		treeSearch.search();
		assertEquals(1, results.size());
	}

	@Test
	public void testFork_NotRunning() throws InterruptedException {
		TestLockingPosition testUnlockedLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						new TestLockingNode(false)));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> strategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = new GameTreeSearch<>(null, testUnlockedLockingPosition, 0, 1, strategy, moveResult -> results.add(moveResult.getSecond()));

		List<GameTreeSearch<TestLockingNode, TestLockingPosition>> trees = treeSearch.fork();

		for (GameTreeSearch<TestLockingNode, TestLockingPosition> subTreeSearch : trees) {
			subTreeSearch.search();
		}

		assertEquals(1, results.size());
	}

	@Test
	public void testFork_EmptyTree() throws InterruptedException {
		TestLockingPosition testUnlockedLockingPosition = new TestLockingPosition(
				new TestLockingNode(false));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> strategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = new GameTreeSearch<>(null, testUnlockedLockingPosition, 0, 1, strategy, moveResult -> results.add(moveResult.getSecond()));

		List<GameTreeSearch<TestLockingNode, TestLockingPosition>> treeSearches = treeSearch.fork();

		assertEquals(0, treeSearches.size());
		assertEquals(1, results.size());
	}

	@Test
	public void testFork_Running() throws InterruptedException {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> strategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = new GameTreeSearch<>(null, testLockingPosition, 0, 1, strategy, moveResult -> results.add(moveResult.getSecond()));

		new Thread(() -> treeSearch.search()).start();

		new Thread(() -> {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			lockedNode.unlock();
		}).start();

		Thread.sleep(50);

		List<GameTreeSearch<TestLockingNode, TestLockingPosition>> treeSearches = treeSearch.fork();
		assertEquals(1, treeSearches.size());

		for (GameTreeSearch<TestLockingNode, TestLockingPosition> subTreeSearch : treeSearches) {
			subTreeSearch.search();
		}

		assertEquals(1, results.size());
	}

	@Test
	public void testFork_MultiFork() {
		TestGamePosition position = TestGamePosition.createTestPosition();
		MinimaxStrategy<TestGameNode, TestGamePosition> strategy = new MinimaxStrategy<>(new TestGameEvaluator());
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> tree = new GameTreeSearch<TestGameNode, TestGamePosition>(null, position, 0, 4, strategy, moveResult -> results.add(moveResult.getSecond()));

		List<GameTreeSearch<TestGameNode, TestGamePosition>> multiFork = new ArrayList<>();
		for (GameTreeSearch<TestGameNode, TestGamePosition> subTree : tree.fork()) {
			multiFork.addAll(subTree.fork());
		}
		for (GameTreeSearch<TestGameNode, TestGamePosition> gameTreeSearch : multiFork) {
			gameTreeSearch.search();
		}

		assertEquals("1 -> [6, 5]: 25.0\n"
				+ "2 -> [4, 3]: 17.0", results.get(0).toString());
	}

	@Test
	public void testGetRemainingBranches() {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> strategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = new GameTreeSearch<>(null, testLockingPosition, 0, 1, strategy, moveResult -> results.add(moveResult.getSecond()));
		assertEquals(3, treeSearch.getRemainingBranches());
	}

	@Test
	public void testGetRemainingBranchesWhileRunning() throws InterruptedException {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		MinimaxStrategy<TestLockingNode, TestLockingPosition> strategy = new MinimaxStrategy<>(new TestLockingEvaluator());
		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = new GameTreeSearch<>(null, testLockingPosition, 0, 1, strategy, moveResult -> results.add(moveResult.getSecond()));

		new Thread(() -> treeSearch.search()).start();

		Thread.sleep(50);

		assertEquals(1, treeSearch.getRemainingBranches());

		lockedNode.unlock();

		Thread.sleep(50);

		assertEquals(0, treeSearch.getRemainingBranches());
	}
}
