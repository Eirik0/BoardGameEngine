package bge.game.ultimatetictactoe;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.igame.Coordinate;
import bge.igame.MoveListFactory;
import bge.igame.player.ComputerPlayer;
import bge.strategy.ts.TreeSearchStrategy;
import bge.strategy.ts.forkjoin.ForkJoinTreeSearcher;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory.ForkableType;

public class UTTTComputerPlayerTest {
    @Test
    @Disabled
    public void testStopOnTime_OneWorker() {
        testStopOnTime(1, 2000);
    }

    @Test
    @Disabled
    public void testStopOnTime_TwoWorker() {
        testStopOnTime(2, 2000);
    }

    @Test
    public void testStopOnTime_ThreeWorker() {
        testStopOnTime(3, 2000);
    }

    @Test
    public void testStopOnTime_SixWorker() {
        testStopOnTime(6, 1000);
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
        long extraTime = 100;
        long allottedTime = toWait + extraTime;
        player.getMove(new UltimateTicTacToePosition());
        player.notifyGameEnded();
        long timeTaken = System.currentTimeMillis() - start;
        System.out.println("Stopped " + numWorkers + " workers in " + (timeTaken - toWait) + "ms");
        assertTrue(timeTaken < allottedTime, Long.toString(timeTaken - allottedTime) + "ms over");
    }

    private static ComputerPlayer newComputerPlayer(int numWorkers, long toWait) {
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
        ForkJoinTreeSearcher<Coordinate, UltimateTicTacToePosition> treeSearcher = new ForkJoinTreeSearcher<>(
                new ForkableTreeSearchFactory<>(ForkableType.MINIMAX, new UltimateTicTacToePositionEvaluator(), moveListFactory),
                moveListFactory, numWorkers);
        return new ComputerPlayer(new TreeSearchStrategy<>(treeSearcher, toWait, true));
    }
}
