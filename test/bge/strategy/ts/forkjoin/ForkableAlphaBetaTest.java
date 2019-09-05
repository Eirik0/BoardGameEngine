package bge.strategy.ts.forkjoin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.game.ultimatetictactoe.UltimateTicTacToeGame;
import bge.game.ultimatetictactoe.UltimateTicTacToePosition;
import bge.game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;
import bge.igame.Coordinate;
import bge.igame.IPosition;
import bge.igame.MoveListFactory;
import bge.strategy.ts.forkjoin.ForkableTreeSearchFactory.ForkableType;

public class ForkableAlphaBetaTest {
    @Test
    public void testAlphaBetaEqualsMinMax() {
        MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);

        compareStrategies(new UltimateTicTacToePosition(), moveListFactory,
                new ForkableTreeSearchFactory<>(ForkableType.MINIMAX, new UltimateTicTacToePositionEvaluator(), moveListFactory),
                new ForkableTreeSearchFactory<>(ForkableType.ALPHA_BETA, new UltimateTicTacToePositionEvaluator(), moveListFactory),
                4, 6);
    }

    public static <M, P extends IPosition<M>> void compareStrategies(P position, MoveListFactory<M> moveListFactory, ForkableTreeSearchFactory<M, P> strat1,
            ForkableTreeSearchFactory<M, P> strat2, int numThreads, int maxPlies) {
        ForkJoinTreeSearcher<M, P> s1 = new ForkJoinTreeSearcher<>(strat1, moveListFactory, numThreads);
        ForkJoinTreeSearcher<M, P> s2 = new ForkJoinTreeSearcher<>(strat2, moveListFactory, numThreads);
        System.out.println("S1: " + strat1.getClass().getSimpleName() + ", S2: " + strat2.getClass().getSimpleName());
        int plies = 0;
        do {
            long start1 = System.currentTimeMillis();
            AnalysisResult<M> s1Result = s1.startSearch(position, plies, true);
            long s1Time = System.currentTimeMillis() - start1;
            long start2 = System.currentTimeMillis();
            AnalysisResult<M> s2Result = s2.startSearch(position, plies, true);
            long s2Time = System.currentTimeMillis() - start2;
            System.out.println("S1: " + s1Time + "ms, S2: " + s2Time + "ms, depth: " + plies + ", " + s1Result.getBestMove(s1Result.getPlayer()).toString());
            assertEquals(s1Result.getBestMove(s1Result.getPlayer()).score, s2Result.getBestMove(s2Result.getPlayer()).score,
                    "Comparing score " + plies);
            Map<M, Double> s2MoveMap = s2Result.getMovesWithScore();
            for (Entry<M, Double> moveWithScore : s1Result.getMovesWithScore().entrySet()) {
                if (!(Math.abs(moveWithScore.getValue() - s2MoveMap.get(moveWithScore.getKey())) < 0.001)) {
                    printResults(s1Result, s2Result);
                    fail(moveWithScore + " " + s2MoveMap.get(moveWithScore.getKey()));
                }
            }
            ++plies;
        } while (plies <= maxPlies);

        s1.stopSearch(true);
        s2.stopSearch(true);
    }

    private static <M> void printResults(AnalysisResult<M> s1Result, AnalysisResult<M> s2Result) {
        Map<M, Double> s1MovesWithScore = s1Result.getMovesWithScore();
        Map<M, Double> s2MovesWithScore = s2Result.getMovesWithScore();
        ArrayList<M> moves = new ArrayList<>(s1MovesWithScore.keySet());
        moves.addAll(s2MovesWithScore.keySet());
        for (M move : moves) {
            System.out.println(move.toString() + " " + s1MovesWithScore.get(move) + " " + s2MovesWithScore.get(move));
        }
    }
}
