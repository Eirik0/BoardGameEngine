package bge.analysis.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveAnalysis;
import bge.analysis.search.GameTreeSearch;
import bge.game.ArrayMoveList;
import bge.game.IPosition;
import bge.game.MoveList;
import bge.game.MoveListFactory;
import bge.game.lock.TestLockingEvaluator;
import bge.game.lock.TestLockingNode;
import bge.game.lock.TestLockingPosition;
import bge.game.value.TestGameEvaluator;
import bge.game.value.TestGameNode;
import bge.game.value.TestGamePosition;

public class MinimaxStrategyTest {
    private synchronized <M, P extends IPosition<M>> AnalysisResult<M> search(MinimaxStrategy<M, P> minimaxStrategy, P testGamePosition, int plies) {
        List<AnalysisResult<M>> result = new ArrayList<>();
        MoveListFactory<M> moveListFactory = new MoveListFactory<>(2);
        MoveList<M> moveList = moveListFactory.newAnalysisMoveList();
        testGamePosition.getPossibleMoves(moveList);
        GameTreeSearch<M, P> gameTreeSearch = new GameTreeSearch<>(
                new MinimaxSearch<>(null, testGamePosition, moveList, moveListFactory, plies, minimaxStrategy),
                (canceled, moveWithResult) -> {
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
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 0);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertNull(result.getBestMoves().get(0));
        assertEquals("null move: 0.0", result.toString());
    }

    @Test
    public void testDepth_One() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 1);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertEquals(-2, result.getBestMoves().get(0).getValue());
        assertEquals("-1 -> [6, 5]: 1.0\n"
                + "-2 -> [4, 3]: 2.0", result.toString());
    }

    @Test
    public void testDepth_Two() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 2);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertEquals(-1, result.getBestMoves().get(0).getValue());
        assertEquals("-1 -> [6, 5]: 5.0\n"
                + "-2 -> [4, 3]: 3.0", result.toString());
    }

    @Test
    public void testDepth_Three() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 3);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertEquals(-2, result.getBestMoves().get(0).getValue());
        assertEquals("-1 -> [6, 5]: 8.0\n"
                + "-2 -> [4, 3]: 12.0", result.toString());
    }

    @Test
    public void testDepth_Four() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 4);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertEquals(-1, result.getBestMoves().get(0).getValue());
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", result.toString());
    }

    @Test
    public void testDepth_Five() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 5);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertEquals(-1, result.getBestMoves().get(0).getValue());
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
        GameTreeSearch<TestLockingNode, TestLockingPosition> gameTreeSearch = new GameTreeSearch<>(
                new MinimaxSearch<>(null, testLockingPosition, moveList, moveListFactory, 1,
                        new MinimaxStrategy<>(new TestLockingEvaluator(), new MoveListProvider<>(moveListFactory))),
                (canceled, moveWithResult) -> result.add(moveWithResult.result));

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
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));
        Map<TestGameNode, AnalysisResult<TestGameNode>> movesWithScore = new LinkedHashMap<>();
        MoveList<TestGameNode> possibleMoves = new ArrayMoveList<>(2);
        testGamePosition.getPossibleMoves(possibleMoves);
        int i = 0;
        while (i < possibleMoves.size()) {
            TestGameNode move = possibleMoves.get(i);
            testGamePosition.makeMove(move);
            movesWithScore.put(move, search(minimaxStrategy, testGamePosition, 3));
            testGamePosition.unmakeMove(move);
            ++i;
        }
        AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(testGamePosition.getCurrentPlayer());
        minimaxStrategy.join(testGamePosition, partialResult, movesWithScore);
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }

    @Test
    public void testJoin_MovesWithScore() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));
        Map<TestGameNode, MoveAnalysis> movesWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore();
        AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(testGamePosition.getCurrentPlayer());
        for (Entry<TestGameNode, MoveAnalysis> moveWithScore : movesWithScore.entrySet()) {
            partialResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
        }
        minimaxStrategy.join(testGamePosition, partialResult, Collections.emptyMap());
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }

    @Test
    public void testJoin_Mixed() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxStrategy<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxStrategy<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        Entry<TestGameNode, MoveAnalysis> moveWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore().entrySet().iterator().next();
        AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(testGamePosition.getCurrentPlayer(), moveWithScore.getKey(),
                moveWithScore.getValue().score);

        MoveList<TestGameNode> possibleMoves = new ArrayMoveList<>(2);
        testGamePosition.getPossibleMoves(possibleMoves);

        TestGameNode move = possibleMoves.get(1);
        testGamePosition.makeMove(move);
        AnalysisResult<TestGameNode> score = search(minimaxStrategy, testGamePosition, 3);
        testGamePosition.unmakeMove(move);

        Map<TestGameNode, AnalysisResult<TestGameNode>> secondResult = Collections.singletonMap(move, score);
        minimaxStrategy.join(testGamePosition, partialResult, secondResult);
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }
}
