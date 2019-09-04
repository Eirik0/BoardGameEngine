package bge.strategy.ts.forkjoin;

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
import bge.game.lock.TestLockingEvaluator;
import bge.game.lock.TestLockingNode;
import bge.game.lock.TestLockingPosition;
import bge.game.value.TestGameEvaluator;
import bge.game.value.TestGameNode;
import bge.game.value.TestGamePosition;
import bge.igame.ArrayMoveList;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.ForkJoinNode;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimax;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxForker;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;

public class ForkableMinimaxTest {
    private synchronized <M, P extends IPosition<M>> AnalysisResult<M> search(MinimaxPositionEvaluator<M, P> minimaxStrategy, P testGamePosition, int plies) {
        List<AnalysisResult<M>> result = new ArrayList<>();
        MoveListFactory<M> moveListFactory = new MoveListFactory<>(2);
        MoveList<M> moveList = moveListFactory.newAnalysisMoveList();
        testGamePosition.getPossibleMoves(moveList);
        ForkJoinNode<M> gameTreeSearch = new ForkJoinNode<>(null,
                new ForkableMinimax<>(testGamePosition, moveList, moveListFactory, plies, minimaxStrategy, new ForkableMinimaxFactory<>(minimaxStrategy)),
                (canceled, moveWithResult) -> {
                    synchronized (this) {
                        result.add(moveWithResult.getSecond());
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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        AnalysisResult<TestGameNode> result = search(minimaxStrategy, testGamePosition, 0);

        assertEquals("0 -> [-1, -2]", testGamePosition.toString());
        assertNull(result.getBestMoves().get(0));
        assertEquals("null move: 0.0", result.toString());
    }

    @Test
    public void testDepth_One() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        ForkableMinimaxFactory<TestLockingNode, TestLockingPosition> forkableFactory = new ForkableMinimaxFactory<>(
                new MinimaxPositionEvaluator<>(new TestLockingEvaluator(), new MoveListProvider<>(moveListFactory)));
        ForkJoinNode<TestLockingNode> gameTreeSearch = new ForkJoinNode<>(null,
                forkableFactory.createNew(testLockingPosition, moveList, moveListFactory, 1),
                (canceled, moveWithResult) -> result.add(moveWithResult.getSecond()));

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
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
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
        MinimaxForker.combineWithPartial(partialResult, movesWithScore);
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }

    @Test
    public void testJoin_MovesWithScore() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));
        Map<TestGameNode, Double> movesWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore();
        AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(testGamePosition.getCurrentPlayer());
        for (Entry<TestGameNode, Double> moveWithScore : movesWithScore.entrySet()) {
            partialResult.addMoveWithScore(moveWithScore.getKey(), moveWithScore.getValue());
        }
        MinimaxForker.combineWithPartial(partialResult, Collections.emptyMap());
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }

    @Test
    public void testJoin_Mixed() {
        TestGamePosition testGamePosition = TestGamePosition.createTestPosition();
        MinimaxPositionEvaluator<TestGameNode, TestGamePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(new TestGameEvaluator(),
                new MoveListProvider<>(new MoveListFactory<>(2)));

        Entry<TestGameNode, Double> moveWithScore = search(minimaxStrategy, testGamePosition, 4).getMovesWithScore().entrySet().iterator().next();
        AnalysisResult<TestGameNode> partialResult = new AnalysisResult<>(testGamePosition.getCurrentPlayer(), moveWithScore.getKey(),
                moveWithScore.getValue());

        MoveList<TestGameNode> possibleMoves = new ArrayMoveList<>(2);
        testGamePosition.getPossibleMoves(possibleMoves);

        TestGameNode move = possibleMoves.get(1);
        testGamePosition.makeMove(move);
        AnalysisResult<TestGameNode> score = search(minimaxStrategy, testGamePosition, 3);
        testGamePosition.unmakeMove(move);

        Map<TestGameNode, AnalysisResult<TestGameNode>> secondResult = Collections.singletonMap(move, score);
        MinimaxForker.combineWithPartial(partialResult, secondResult);
        assertEquals("-1 -> [6, 5]: 25.0\n"
                + "-2 -> [4, 3]: 17.0", partialResult.toString());
    }
}
