package bge.igame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

import org.junit.jupiter.api.Test;

import bge.igame.player.GuiPlayer;
import bge.igame.player.IPlayer;

public class GameRunnerTest {
    private static void startGame(GameRunner<?> gameRunner, IPlayer player) {
        gameRunner.createNewGame();
        gameRunner.setPlayersAndResume(Collections.singletonList(player));
    }

    @Test
    public void testStartStopGame() throws InterruptedException {
        AddToListTestGame game = new AddToListTestGame();
        GameRunner<?> gameRunner = new GameRunner<>(game, new GameObserver<>(), new MoveListFactory<>(1));
        startGame(gameRunner, game.player);
        Thread.sleep(10);// sleep a little to let the list populate
        gameRunner.pauseGame(false);
        assertTrue(game.list.size() > 0);
        assertEquals(2, game.numNewPositions);
    }

    @Test
    public void testStartTwice() throws InterruptedException {
        AddToListTestGame game = new AddToListTestGame();
        GameRunner<?> gameRunner = new GameRunner<>(game, new GameObserver<>(), new MoveListFactory<>(1));
        startGame(gameRunner, game.player);
        startGame(gameRunner, game.player);
        Thread.sleep(10);// sleep a little to let the list populate
        gameRunner.pauseGame(false);
        assertTrue(game.list.size() > 0);
        assertEquals(3, game.numNewPositions);
    }

    @Test
    public void testEndTwice() throws InterruptedException {
        AddToListTestGame game = new AddToListTestGame();
        GameRunner<?> gameRunner = new GameRunner<>(game, new GameObserver<>(), new MoveListFactory<>(1));
        startGame(gameRunner, game.player);
        Thread.sleep(10);// sleep a little to let the list populate
        gameRunner.pauseGame(false);
        gameRunner.pauseGame(false);
        assertTrue(game.list.size() > 0);
        assertEquals(2, game.numNewPositions);
    }

    @Test
    public void testEndWhenWaitingOnPlayer() throws InterruptedException {
        AddToListTestGame game = new AddToListTestGame(GuiPlayer.HUMAN);
        GameRunner<?> gameRunner = new GameRunner<>(game, new GameObserver<>(), new MoveListFactory<>(1));
        startGame(gameRunner, game.player);
        Thread.sleep(10);
        gameRunner.pauseGame(false);
        assertEquals(2, game.numNewPositions);
    }

    @Test
    public void testStardAndEndWhenNoMoves() {
        AddToListTestGame game = new AddToListTestGame(new AddToListTestPlayer(), i -> Collections.emptyList());
        GameRunner<?> gameRunner = new GameRunner<>(game, new GameObserver<>(), new MoveListFactory<>(1));
        startGame(gameRunner, game.player);
        assertEquals(2, game.numNewPositions);
    }

    static class AddToListTestGame implements IGame<Integer> {
        final IPlayer player;
        final List<Integer> list = new ArrayList<>();
        final IntFunction<List<Integer>> possibleMovesFunction;
        int numNewPositions = 0;

        public AddToListTestGame() {
            this(new AddToListTestPlayer());
        }

        public AddToListTestGame(IPlayer player) {
            this(player, i -> Collections.singletonList(Integer.valueOf(i)));
        }

        public AddToListTestGame(IPlayer player, IntFunction<List<Integer>> possibleMovesFunction) {
            this.player = player;
            this.possibleMovesFunction = possibleMovesFunction;
        }

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        public int getNumberOfPlayers() {
            return 1;
        }

        @Override
        public int getMaxMoves() {
            return 0; // Unused
        }

        @Override
        public AddToListPosition newInitialPosition() {
            numNewPositions++;
            list.clear();
            return new AddToListPosition(0, list, possibleMovesFunction);
        }
    }

    static class AddToListTestPlayer implements IPlayer {
        @Override
        public <M> M getMove(IPosition<M> position) {
            MoveList<M> possibleMoves = new ArrayMoveList<>(1);
            position.getPossibleMoves(possibleMoves);
            return possibleMoves.get(0);
        }

        @Override
        public void notifyTurnEnded() {
        }

        @Override
        public void notifyGameEnded() {
        }
    }

    static class AddToListPosition implements IPosition<Integer> {
        int index;
        final List<Integer> list;
        final IntFunction<List<Integer>> possibleMovesFunction;

        private AddToListPosition(int index, List<Integer> list, IntFunction<List<Integer>> possibleMovesFunction) {
            this.index = index;
            this.list = list;
            this.possibleMovesFunction = possibleMovesFunction;
        }

        @Override
        public void getPossibleMoves(MoveList<Integer> moveList) {
            List<Integer> moves = possibleMovesFunction.apply(index);
            moveList.addAllQuietMoves(moves.toArray(new Integer[moves.size()]), this);
        }

        @Override
        public int getCurrentPlayer() {
            return 1;
        }

        @Override
        public void makeMove(Integer move) {
            list.add(move);
            ++index;
        }

        @Override
        public void unmakeMove(Integer move) {
            list.remove(move);
            --index;
        }

        @Override
        public AddToListPosition createCopy() {
            return new AddToListPosition(index, new ArrayList<>(list), possibleMovesFunction);
        }
    }
}
