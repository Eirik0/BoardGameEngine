package analysis.search;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import analysis.strategy.MinimaxStrategy;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import game.lock.TestLockingEvaluator;
import game.lock.TestLockingNode;
import game.lock.TestLockingPosition;
import game.value.TestGameEvaluator;
import game.value.TestGameNode;
import game.value.TestGamePosition;

public class GameTreeSearchTest {
	private static <M, P extends IPosition<M>> GameTreeSearch<M, P> newGameTreeSearch(P position, int plies, int maxMoves, IPositionEvaluator<M, P> positionEvaluator,
			List<AnalysisResult<M>> results) {
		MoveListFactory<M> moveListFactory = new MoveListFactory<>(maxMoves);
		MinimaxStrategy<M, P> strategy = new MinimaxStrategy<>(moveListFactory, positionEvaluator);
		MoveList<M> moveList = moveListFactory.newAnalysisMoveList();
		position.getPossibleMoves(moveList);
		return new GameTreeSearch<M, P>(null, position, moveList, moveListFactory, plies, strategy, (canceled, player, moveWithResult) -> results.add(moveWithResult.result));
	}

	@Test
	public void testSearch() {
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> treeSearch = newGameTreeSearch(TestGamePosition.createTestPosition(), 1, 2, new TestGameEvaluator(), results);
		treeSearch.search();
		assertEquals(1, results.size());
	}

	@Test
	public void testFork_NotRunning() {
		TestLockingPosition testUnlockedLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						new TestLockingNode(false)));

		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = newGameTreeSearch(testUnlockedLockingPosition, 1, 3, new TestLockingEvaluator(), results);

		List<GameTreeSearch<TestLockingNode, TestLockingPosition>> trees = treeSearch.fork();

		for (GameTreeSearch<TestLockingNode, TestLockingPosition> subTreeSearch : trees) {
			subTreeSearch.search();
		}

		assertEquals(1, results.size());
	}

	@Test
	public void testFork() {
		TestLockingPosition testUnlockedLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false)));

		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = newGameTreeSearch(testUnlockedLockingPosition, 1, 2, new TestLockingEvaluator(), results);

		List<GameTreeSearch<TestLockingNode, TestLockingPosition>> treeSearches = treeSearch.fork();

		assertEquals(2, treeSearches.size());
		treeSearches.get(0).search();
		treeSearches.get(1).search();
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

		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);

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
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 4, 2, new TestGameEvaluator(), results);

		List<GameTreeSearch<TestGameNode, TestGamePosition>> multiFork = new ArrayList<>();
		for (GameTreeSearch<TestGameNode, TestGamePosition> subTree : tree.fork()) {
			multiFork.addAll(subTree.fork());
		}

		for (GameTreeSearch<TestGameNode, TestGamePosition> gameTreeSearch : multiFork) {
			gameTreeSearch.search();
		}

		assertEquals("-1 -> [6, 5]: 25.0\n"
				+ "-2 -> [4, 3]: 17.0", results.get(0).toString());
	}

	@Test
	public void testGetRemainingBranches() {
		TestLockingNode lockedNode = new TestLockingNode(true);

		TestLockingPosition testLockingPosition = new TestLockingPosition(
				new TestLockingNode(false).setMoves(
						new TestLockingNode(false),
						new TestLockingNode(false),
						lockedNode));

		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);
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

		List<AnalysisResult<TestLockingNode>> results = new ArrayList<>();
		GameTreeSearch<TestLockingNode, TestLockingPosition> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);

		new Thread(() -> treeSearch.search()).start();

		Thread.sleep(50);

		assertEquals(1, treeSearch.getRemainingBranches());

		lockedNode.unlock();

		Thread.sleep(50);

		assertEquals(0, treeSearch.getRemainingBranches());
	}

	@Test
	public void testDoNotIncludeUnfinishedForks() {
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 3, 2, new TestGameEvaluator(), results);

		List<GameTreeSearch<TestGameNode, TestGamePosition>> fork = tree.fork();
		List<GameTreeSearch<TestGameNode, TestGamePosition>> forkPos1 = fork.get(0).fork();
		List<GameTreeSearch<TestGameNode, TestGamePosition>> forkPos2 = fork.get(1).fork();
		GameTreeSearch<TestGameNode, TestGamePosition> pos6 = forkPos1.get(0);
		GameTreeSearch<TestGameNode, TestGamePosition> pos5 = forkPos1.get(1);
		GameTreeSearch<TestGameNode, TestGamePosition> pos4 = forkPos2.get(0);
		GameTreeSearch<TestGameNode, TestGamePosition> pos3 = forkPos2.get(1);
		pos6.search();
		pos5.search();
		pos4.search();
		pos3.stopSearch();
		pos3.search(); // consumes the result
		AnalysisResult<TestGameNode> analysisResult = results.get(0);
		assertEquals(1, analysisResult.getMovesWithScore().size());
		assertEquals(1, analysisResult.getInvalidMoves().size());
	}

	@Test
	public void testRootIncludesFinishedForks() {
		List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
		GameTreeSearch<TestGameNode, TestGamePosition> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 4, 2, new TestGameEvaluator(), results);

		List<GameTreeSearch<TestGameNode, TestGamePosition>> fork = tree.fork();
		List<GameTreeSearch<TestGameNode, TestGamePosition>> forkPos1 = fork.get(0).fork();
		List<GameTreeSearch<TestGameNode, TestGamePosition>> forkPos2 = fork.get(1).fork();
		GameTreeSearch<TestGameNode, TestGamePosition> pos6 = forkPos1.get(0);
		GameTreeSearch<TestGameNode, TestGamePosition> pos4 = forkPos2.get(0);
		GameTreeSearch<TestGameNode, TestGamePosition> pos3 = forkPos2.get(1);
		List<GameTreeSearch<TestGameNode, TestGamePosition>> forkPos5 = forkPos1.get(1).fork();
		GameTreeSearch<TestGameNode, TestGamePosition> pos9 = forkPos5.get(0);
		GameTreeSearch<TestGameNode, TestGamePosition> pos10 = forkPos5.get(1);
		pos6.search();
		pos4.search();
		pos3.search();
		pos9.search();
		pos10.stopSearch();
		pos10.search(); // consumes the result
		AnalysisResult<TestGameNode> analysisResult = results.get(0);
		assertEquals(1, analysisResult.getMovesWithScore().size());
		assertEquals(1, analysisResult.getInvalidMoves().size());
	}
}
