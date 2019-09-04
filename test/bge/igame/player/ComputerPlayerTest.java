package bge.igame.player;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.game.tictactoe.TicTacToeGame;
import bge.game.tictactoe.TicTacToePosition;
import bge.game.tictactoe.TicTacToePositionEvaluator;
import bge.igame.Coordinate;
import bge.igame.GameObserver;
import bge.igame.GameRunner;
import bge.igame.MoveListFactory;
import bge.strategy.ts.MoveListProvider;
import bge.strategy.ts.TreeSearchStrategy;
import bge.strategy.ts.forkjoin.ForkJoinTreeSearcher;
import bge.strategy.ts.forkjoin.minmax.ForkableMinimaxFactory;
import bge.strategy.ts.forkjoin.minmax.MinimaxPositionEvaluator;

public class ComputerPlayerTest {
    @Test
    @Disabled
    public void testDoNotWaitForAMoveIfFInishedSearching() throws InterruptedException {
        TicTacToeGame game = new TicTacToeGame();
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(TicTacToeGame.MAX_MOVES);
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = new ForkJoinTreeSearcher<>(
                new ForkableMinimaxFactory<>(new MinimaxPositionEvaluator<>(new TicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory))),
                moveListFactory, 2);
        ComputerPlayer player = new ComputerPlayer(new TreeSearchStrategy<>(treeSearcher, 500, true));
        GameRunner<Coordinate> gameRunner = new GameRunner<>(game, new GameObserver<>(), moveListFactory);
        for (int i = 0; i < 100; ++i) {
            gameRunner.createNewGame();
            gameRunner.setPlayersAndResume(Arrays.asList(player, player));
            int sleep = new Random().nextInt(1000); // To reset the game randomly
            System.out.println(i + 1 + ": " + sleep);
            Thread.sleep(sleep);
        }
    }
}
