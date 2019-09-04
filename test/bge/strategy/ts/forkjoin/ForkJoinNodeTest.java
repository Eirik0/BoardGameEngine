package bge.strategy.ts.forkjoin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.lock.TestLockingEvaluator;
import bge.game.lock.TestLockingNode;
import bge.game.lock.TestLockingPosition;
import bge.game.value.TestGameEvaluator;
import bge.game.value.TestGameNode;
import bge.game.value.TestGamePosition;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;

public class ForkJoinNodeTest {
    private static <M, P extends IPosition<M>> ForkJoinNode<M> newGameTreeSearch(P position, int plies, int maxMoves,
            IPositionEvaluator<M, P> positionEvaluator, List<AnalysisResult<M>> results) {
        MoveListFactory<M> moveListFactory = new MoveListFactory<>(maxMoves);
        MoveListProvider<M> moveListProvider = new MoveListProvider<>(moveListFactory);
        MinimaxPositionEvaluator<M, P> strategy = new MinimaxPositionEvaluator<>(positionEvaluator, moveListProvider);

        MoveList<M> moveList = moveListFactory.newAnalysisMoveList();
        position.getPossibleMoves(moveList);
        return new ForkJoinNode<>(null, new ForkableMinimaxFactory<>(strategy).createNew(position, moveList, moveListFactory, plies),
                (canceled, moveWithResult) -> results.add(moveWithResult.getSecond()));
    }

    @Test
    public void testSearch() {
        List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
        ForkJoinNode<TestGameNode> treeSearch = newGameTreeSearch(TestGamePosition.createTestPosition(), 1, 2, new TestGameEvaluator(), results);
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
        ForkJoinNode<TestLockingNode> treeSearch = newGameTreeSearch(testUnlockedLockingPosition, 1, 3, new TestLockingEvaluator(), results);

        List<ForkJoinNode<TestLockingNode>> trees = treeSearch.fork();

        for (ForkJoinNode<TestLockingNode> subTreeSearch : trees) {
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
        ForkJoinNode<TestLockingNode> treeSearch = newGameTreeSearch(testUnlockedLockingPosition, 1, 2, new TestLockingEvaluator(), results);

        List<ForkJoinNode<TestLockingNode>> treeSearches = treeSearch.fork();

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
        ForkJoinNode<TestLockingNode> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);

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

        List<ForkJoinNode<TestLockingNode>> treeSearches = treeSearch.fork();
        assertEquals(1, treeSearches.size());

        for (ForkJoinNode<TestLockingNode> subTreeSearch : treeSearches) {
            subTreeSearch.search();
        }

        assertEquals(1, results.size());
    }

    @Test
    public void testFork_MultiFork() {
        List<AnalysisResult<TestGameNode>> results = new ArrayList<>();
        ForkJoinNode<TestGameNode> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 4, 2, new TestGameEvaluator(), results);

        List<ForkJoinNode<TestGameNode>> multiFork = new ArrayList<>();
        for (ForkJoinNode<TestGameNode> subTree : tree.fork()) {
            multiFork.addAll(subTree.fork());
        }

        for (ForkJoinNode<TestGameNode> gameTreeSearch : multiFork) {
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
        ForkJoinNode<TestLockingNode> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);
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
        ForkJoinNode<TestLockingNode> treeSearch = newGameTreeSearch(testLockingPosition, 1, 3, new TestLockingEvaluator(), results);

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
        ForkJoinNode<TestGameNode> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 3, 2, new TestGameEvaluator(), results);

        List<ForkJoinNode<TestGameNode>> fork = tree.fork();
        List<ForkJoinNode<TestGameNode>> forkPos1 = fork.get(0).fork();
        List<ForkJoinNode<TestGameNode>> forkPos2 = fork.get(1).fork();
        ForkJoinNode<TestGameNode> pos6 = forkPos1.get(0);
        ForkJoinNode<TestGameNode> pos5 = forkPos1.get(1);
        ForkJoinNode<TestGameNode> pos4 = forkPos2.get(0);
        ForkJoinNode<TestGameNode> pos3 = forkPos2.get(1);
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
        ForkJoinNode<TestGameNode> tree = newGameTreeSearch(TestGamePosition.createTestPosition(), 4, 2, new TestGameEvaluator(), results);

        List<ForkJoinNode<TestGameNode>> fork = tree.fork();
        List<ForkJoinNode<TestGameNode>> forkPos1 = fork.get(0).fork();
        List<ForkJoinNode<TestGameNode>> forkPos2 = fork.get(1).fork();
        ForkJoinNode<TestGameNode> pos6 = forkPos1.get(0);
        ForkJoinNode<TestGameNode> pos4 = forkPos2.get(0);
        ForkJoinNode<TestGameNode> pos3 = forkPos2.get(1);
        List<ForkJoinNode<TestGameNode>> forkPos5 = forkPos1.get(1).fork();
        ForkJoinNode<TestGameNode> pos9 = forkPos5.get(0);
        ForkJoinNode<TestGameNode> pos10 = forkPos5.get(1);
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
