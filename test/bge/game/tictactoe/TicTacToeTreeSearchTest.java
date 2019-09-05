package bge.game.tictactoe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.igame.Coordinate;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkJoinTreeSearcher;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory.ForkableType;

public class TicTacToeTreeSearchTest {
    private static ForkJoinTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher() {
        return newTreeSearcher(1);
    }

    private static ForkJoinTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher(int numWorkers) {
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(TicTacToeGame.MAX_MOVES);
        return new ForkJoinTreeSearcher<>(new ForkableTreeSearchFactory<>(ForkableType.MINIMAX, new TicTacToePositionEvaluator(), moveListFactory),
                moveListFactory, numWorkers);
    }

    @Test
    public void testSearchTicTacToe() {
        TicTacToePosition position = new TicTacToePosition();
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
        AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 1, true);
        assertEquals(Coordinate.valueOf(0, 0), search.getBestMove(search.getPlayer()).move);
        treeSearcher.stopSearch(true);
    }

    @Test
    public void testSearchTicTacToe_TestSearchTwoPlies() {
        TicTacToePosition position = new TicTacToePosition();
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
        AnalysisResult<Coordinate> result = treeSearcher.startSearch(position, 2, true);
        treeSearcher.stopSearch(true);
        assertEquals(Coordinate.valueOf(0, 0), result.getBestMoves().get(0));
    }

    @Test
    public void testSearchTicTacToe_TestPlayGame() {
        TicTacToePosition position = new TicTacToePosition();
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
        searchAndMove(treeSearcher, position, 2);
        assertEquals("[X  ],[   ],[   ]", position.toString());
        searchAndMove(treeSearcher, position, 2);
        assertEquals("[XO ],[   ],[   ]", position.toString());
        searchAndMove(treeSearcher, position, 2);
        assertEquals("[XOX],[   ],[   ]", position.toString());
        searchAndMove(treeSearcher, position, 2);
        assertEquals("[XOX],[O  ],[   ]", position.toString());
        searchAndMove(treeSearcher, position, 2);
        assertEquals("[XOX],[OX ],[   ]", position.toString());
        treeSearcher.stopSearch(true);
    }

    private static void searchAndMove(ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher, TicTacToePosition position, int plies) {
        AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, plies, true);
        position.makeMove(search.getBestMove(search.getPlayer()).move);
    }

    @Test
    public void testFullSearch() {
        TicTacToePosition position = new TicTacToePosition();
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
        AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9, true);
        for (Entry<Coordinate, Double> moveWithScore : search.getMovesWithScore().entrySet()) {
            assertEquals(AnalysisResult.DRAW, moveWithScore.getValue().doubleValue());
        }
        treeSearcher.stopSearch(true);
    }

    @Test
    public void testFindWin() {
        TicTacToePosition position = new TicTacToePosition();
        position.makeMove(Coordinate.valueOf(1, 1));
        position.makeMove(Coordinate.valueOf(0, 1));
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
        AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9, true);
        Coordinate draw = Coordinate.valueOf(2, 1);
        for (Entry<Coordinate, Double> moveWithScore : search.getMovesWithScore().entrySet()) {
            assertEquals(moveWithScore.getKey().equals(draw) ? -0.0 : AnalysisResult.WIN, moveWithScore.getValue().doubleValue(),
                    moveWithScore.getKey().toString());
        }
        treeSearcher.stopSearch(true);
    }

    @Test
    public void testStopSearchIfAllPositionsEvaluated() {
        TicTacToePosition position = new TicTacToePosition();
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher(2);
        treeSearcher.startSearch(position, 9, true);
        assertEquals(9, treeSearcher.getPlies());
        treeSearcher.startSearch(position, 10, true);
        assertEquals(9, treeSearcher.getPlies());
        treeSearcher.startSearch(position, 11, true);
        assertEquals(9, treeSearcher.getPlies());
        treeSearcher.stopSearch(true);
    }

    @Test
    public void testDefendAgainstLoss() {
        TicTacToePosition position = new TicTacToePosition();
        position.makeMove(Coordinate.valueOf(0, 0));
        position.makeMove(Coordinate.valueOf(1, 1));
        position.makeMove(Coordinate.valueOf(2, 0));
        position.makeMove(Coordinate.valueOf(1, 0));
        position.makeMove(Coordinate.valueOf(1, 2));
        position.makeMove(Coordinate.valueOf(0, 1));
        position.makeMove(Coordinate.valueOf(2, 1));
        ForkJoinTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher(2);
        for (int i = 0; i < 1000; ++i) {
            AnalysisResult<Coordinate> result = treeSearcher.startSearch(position, 11, true);
            assertEquals(2, treeSearcher.getPlies());
            Map<Coordinate, Double> movesWithScore = result.getMovesWithScore();
            String assertMessage = "search " + String.valueOf(i) + ":\n" + result.toString();
            assertEquals(2, movesWithScore.size(), assertMessage);
            assertEquals(AnalysisResult.DRAW, movesWithScore.get(Coordinate.valueOf(2, 2)).doubleValue(), assertMessage);
            assertEquals(AnalysisResult.LOSS, movesWithScore.get(Coordinate.valueOf(0, 2)).doubleValue(), assertMessage);
        }
        treeSearcher.stopSearch(true);
    }
}
