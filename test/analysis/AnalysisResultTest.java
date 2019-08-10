package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import util.Pair;

public class AnalysisResultTest {
    @Test
    public void testMerged() {
        AnalysisResult<Integer> result1 = createResult(Arrays.asList(Pair.valueOf(1, 1.0), Pair.valueOf(2, 1.0), Pair.valueOf(3, 1.0), Pair.valueOf(4, 1.0)));
        AnalysisResult<Integer> result2 = createResult(Arrays.asList(Pair.valueOf(1, 2.0), Pair.valueOf(2, 0.5)));
        AnalysisResult<Integer> mergedRestult = result1.mergeWith(result2);
        assertEquals(4, result1.getMovesWithScore().size());
        Map<Integer, MoveAnalysis> movesWithScore = mergedRestult.getMovesWithScore();
        assertEquals(2.0, movesWithScore.get(Integer.valueOf(1)).score, 0.0);
        assertEquals(0.5, movesWithScore.get(Integer.valueOf(2)).score, 0.0);
        assertEquals(1.0, movesWithScore.get(Integer.valueOf(3)).score, 0.0);
        assertEquals(1.0, movesWithScore.get(Integer.valueOf(4)).score, 0.0);
        AnalyzedMove<Integer> bestMove = mergedRestult.getBestMove(mergedRestult.getPlayer());
        assertEquals(2.0, bestMove.analysis.score, 0.0);
        assertEquals(Integer.valueOf(1), bestMove.move);
    }

    private static AnalysisResult<Integer> createResult(List<Pair<Integer, Double>> movesWithScore) {
        AnalysisResult<Integer> result = new AnalysisResult<>(1);
        for (Pair<Integer, Double> moveWithScore : movesWithScore) {
            result.addMoveWithScore(moveWithScore.getFirst(), new MoveAnalysis(moveWithScore.getSecond().doubleValue()));
        }
        return result;
    }

    @Test
    public void testFindBestMoveEvenIfLost() {
        AnalysisResult<String> result = new AnalysisResult<>(1);
        result.addMoveWithScore("1", new MoveAnalysis(AnalysisResult.LOSS));
        assertEquals("1", result.getBestMove(result.getPlayer()).move);
    }

    @Test
    public void testGreaterThan() {
        assertTrue("2 > 1", AnalysisResult.isGreater(2, 1));
        assertFalse("1 > 2", AnalysisResult.isGreater(1, 2));
        assertFalse("0 > 0", AnalysisResult.isGreater(0, 0));
        assertTrue("0 > Draw", AnalysisResult.isGreater(0, AnalysisResult.DRAW));
        assertTrue("1 > Draw", AnalysisResult.isGreater(0, AnalysisResult.DRAW));
        assertFalse("-1 > Draw", AnalysisResult.isGreater(-1, AnalysisResult.DRAW));
        assertTrue("Draw > -1", AnalysisResult.isGreater(AnalysisResult.DRAW, -1));
        assertFalse("Draw > 0", AnalysisResult.isGreater(AnalysisResult.DRAW, 0));
        assertFalse("Draw > 1", AnalysisResult.isGreater(AnalysisResult.DRAW, 1));
        assertFalse("Draw > Draw", AnalysisResult.isGreater(AnalysisResult.DRAW, AnalysisResult.DRAW));
    }
}
