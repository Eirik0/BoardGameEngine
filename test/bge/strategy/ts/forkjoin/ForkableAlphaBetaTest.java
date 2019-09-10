package bge.strategy.ts.forkjoin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import bge.analysis.AnalysisResult;
import bge.analysis.MoveWithScore;
import bge.analysis.MoveWithScoreFinder;
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
            List<MoveWithScore<M>> s1Moves = s1Result.getMovesWithScore();
            List<MoveWithScore<M>> s2Moves = s2Result.getMovesWithScore();
            assertEquals(s1Moves.size(), s2Moves.size());
            for (MoveWithScore<M> s1moveWithScore : s1Moves) {
                MoveWithScore<M> s2MoveWithScore = MoveWithScoreFinder.find(s2Moves, s1moveWithScore.move);
                if (!(Math.abs(s1moveWithScore.score - s2MoveWithScore.score) < 0.001)) {
                    printResults(s1Result, s2Result);
                    fail(s1moveWithScore + " " + s2MoveWithScore);
                }
            }
            ++plies;
        } while (plies <= maxPlies);

        s1.stopSearch(true);
        s2.stopSearch(true);
    }

    private static <M> void printResults(AnalysisResult<M> s1Result, AnalysisResult<M> s2Result) {
        List<MoveWithScore<M>> s1MovesWithScore = s1Result.getMovesWithScore();
        List<MoveWithScore<M>> s2MovesWithScore = s2Result.getMovesWithScore();
        for (MoveWithScore<M> s1MoveWithScore : s1MovesWithScore) {
            double s2Score = MoveWithScoreFinder.find(s2MovesWithScore, s1MoveWithScore.move).score;
            System.out.println(s1MoveWithScore.move + " " + s1MoveWithScore.score + " " + s2Score);
        }
    }
}
