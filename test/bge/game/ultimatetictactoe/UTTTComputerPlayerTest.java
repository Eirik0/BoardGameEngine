package bge.game.ultimatetictactoe;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import bge.analysis.ComputerPlayer;
import bge.analysis.search.IterativeDeepeningTreeSearcher;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.game.Coordinate;
import bge.game.MoveListFactory;

public class UTTTComputerPlayerTest {
    @Test
    @Ignore
    public void testStopOnTime_OneWorker() {
        testStopOnTime(1, 2000);
    }

    @Test
    @Ignore
    public void testStopOnTime_TwoWorker() {
        testStopOnTime(2, 2000);
    }

    @Test
    public void testStopOnTime_ThreeWorker() {
        testStopOnTime(3, 2000);
    }

    @Test
    public void testMakeTwoMoves() {
        ComputerPlayer player = newComputerPlayer(2, 50);
        UltimateTicTacToeUtilities.initialize();
        UltimateTicTacToePosition position = new UltimateTicTacToePosition();
        position.makeMove(player.getMove(position));
        position.makeMove(player.getMove(position));
        player.notifyGameEnded();
    }

    private static void testStopOnTime(int numWorkers, long toWait) {
        ComputerPlayer player = newComputerPlayer(numWorkers, toWait);
        long start = System.currentTimeMillis();
        long extraTime = 1000;
        long allottedTime = toWait + extraTime;
        player.getMove(new UltimateTicTacToePosition());
        player.notifyGameEnded();
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Stopped " + numWorkers + " workers in " + (timeTaken - toWait) + "ms");
        assertTrue(Long.toString(timeTaken - allottedTime) + "ms over", timeTaken < allottedTime);
    }

    private static ComputerPlayer newComputerPlayer(int numWorkers, long toWait) {
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
        return new ComputerPlayer("MinMax",
                new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory)),
                        moveListFactory, numWorkers),
                numWorkers, toWait, true);
    }
}