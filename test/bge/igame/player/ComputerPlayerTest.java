package bge.igame.player;

import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.analysis.search.IterativeDeepeningTreeSearcher;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.game.tictactoe.TicTacToeGame;
import bge.game.tictactoe.TicTacToePosition;
import bge.game.tictactoe.TicTacToePositionEvaluator;
import bge.igame.Coordinate;
import bge.igame.GameObserver;
import bge.igame.GameRunner;
import bge.igame.MoveHistory;
import bge.igame.MoveListFactory;

public class ComputerPlayerTest {
    @Test
    @Disabled
    public void testDoNotWaitForAMoveIfFInishedSearching() throws InterruptedException {
        TicTacToeGame game = new TicTacToeGame();
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(TicTacToeGame.MAX_MOVES);
        ComputerPlayer player = new ComputerPlayer(new IterativeDeepeningTreeSearcher<>(
                new MinimaxStrategy<>(new TicTacToePositionEvaluator(), new MoveListProvider<>(moveListFactory)), moveListFactory, 2),
                500, true);
        MoveHistory<Coordinate> moveHistory = new MoveHistory<>(new TicTacToeGame());
        GameRunner<Coordinate, TicTacToePosition> gameRunner = new GameRunner<>(game, moveHistory, new GameObserver<>(), moveListFactory);
        for (int i = 0; i < 100; ++i) {
            gameRunner.createNewGame();
            gameRunner.setPlayersAndResume(Arrays.asList(player, player));
            int sleep = new Random().nextInt(1000); // To reset the game randomly
            System.out.println(i + 1 + ": " + sleep);
            Thread.sleep(sleep);
        }
    }
}
