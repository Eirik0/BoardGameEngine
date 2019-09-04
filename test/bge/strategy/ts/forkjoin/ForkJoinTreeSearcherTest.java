package bge.strategy.ts.forkjoin;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.game.ultimatetictactoe.UltimateTicTacToeGame;
import bge.game.ultimatetictactoe.UltimateTicTacToePosition;
import bge.game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;
import bge.igame.Coordinate;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;

public class ForkJoinTreeSearcherTest {
    private static void doTest(int numThreads) throws InterruptedException {
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
        MinimaxPositionEvaluator<Coordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(
                new UltimateTicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory));
        ForkJoinTreeSearcher<Coordinate, UltimateTicTacToePosition> iterativeDeepeningStrategy = new ForkJoinTreeSearcher<>(
                new ForkableMinimaxFactory<>(minimaxStrategy), moveListFactory, numThreads);

        iterativeDeepeningStrategy.searchForever(new UltimateTicTacToePosition(), true);
        Thread.sleep(50);
        iterativeDeepeningStrategy.stopSearch(true);
        AnalysisResult<Coordinate> result = iterativeDeepeningStrategy.getResult();
        System.out.println(
                numThreads + " workers, plies: " + iterativeDeepeningStrategy.getPlies() + ", bestMove = " + result.getBestMove(result.getPlayer()).toString());
    }

    @Test
    public void testStartStop_1() throws InterruptedException {
        doTest(1);
    }

    @Test
    public void testStartStop_2() throws InterruptedException {
        doTest(2);
    }

    @Test
    public void testStartStop_3() throws InterruptedException {
        doTest(3);
    }

    @Test
    public void testStartStop_4() throws InterruptedException {
        doTest(4);
    }

    @Test
    public void testStartStop_5() throws InterruptedException {
        doTest(5);
    }

    @Test
    public void testStartStop_6() throws InterruptedException {
        doTest(6);
    }

    @Test
    public void testCompareSpeeds() {
        doSpeedTest(1);
        doSpeedTest(2);
        doSpeedTest(3);
        doSpeedTest(4);
        doSpeedTest(5);
        doSpeedTest(6);
    }

    private static void doSpeedTest(int numThreads) {
        int numPlies = 5;
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
        MinimaxPositionEvaluator<Coordinate, UltimateTicTacToePosition> minimaxStrategy = new MinimaxPositionEvaluator<>(
                new UltimateTicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory));

        ForkJoinTreeSearcher<Coordinate, UltimateTicTacToePosition> iterativeDeepeningSearcher = new ForkJoinTreeSearcher<>(
                new ForkableMinimaxFactory<>(minimaxStrategy), moveListFactory, numThreads);

        long start = System.currentTimeMillis();
        iterativeDeepeningSearcher.startSearch(new UltimateTicTacToePosition(), numPlies, true);
        System.out.println(numThreads + " workers " + numPlies + " plies in " + (System.currentTimeMillis() - start) + "ms");
        iterativeDeepeningSearcher.stopSearch(true);
    }
}
