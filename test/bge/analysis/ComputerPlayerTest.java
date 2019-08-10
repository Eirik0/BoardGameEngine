package bge.analysis;

import java.util.Arrays;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import bge.analysis.search.IterativeDeepeningTreeSearcher;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.game.Coordinate;
import bge.game.GameObserver;
import bge.game.GameRunner;
import bge.game.MoveListFactory;
import bge.game.tictactoe.TicTacToeGame;
import bge.game.tictactoe.TicTacToePosition;
import bge.game.tictactoe.TicTacToePositionEvaluator;

public class ComputerPlayerTest {
    @Test
    @Ignore
    public void testDoNotWaitForAMoveIfFInishedSearching() throws InterruptedException {
        TicTacToeGame game = new TicTacToeGame();
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(TicTacToeGame.MAX_MOVES);
        ComputerPlayer player = new ComputerPlayer("MinMax",
                new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(new TicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory)),
                        moveListFactory, 2),
                2, 500, true);
        GameRunner<Coordinate, TicTacToePosition> gameRunner = new GameRunner<>(game, new GameObserver<>(), moveListFactory);
        for (int i = 0; i < 100; ++i) {
            gameRunner.createNewGame();
            gameRunner.setPlayersAndResume(Arrays.asList(player, player));
            int sleep = new Random().nextInt(1000); // To reset the game randomly
            System.out.println(i + 1 + ": " + sleep);
            Thread.sleep(sleep);
        }
    }
}
