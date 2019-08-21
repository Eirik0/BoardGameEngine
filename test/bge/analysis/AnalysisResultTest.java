package bge.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import bge.util.Pair;

public class AnalysisResultTest {
    @Test
    public void testMerged() {
        AnalysisResult<Integer> result1 = createResult(Arrays.asList(Pair.valueOf(1, 1.0), Pair.valueOf(2, 1.0), Pair.valueOf(3, 1.0), Pair.valueOf(4, 1.0)));
        AnalysisResult<Integer> result2 = createResult(Arrays.asList(Pair.valueOf(1, 2.0), Pair.valueOf(2, 0.5)));
        AnalysisResult<Integer> mergedRestult = result1.mergeWith(result2);
        assertEquals(4, result1.getMovesWithScore().size());
        Map<Integer, MoveAnalysis> movesWithScore = mergedRestult.getMovesWithScore();
        assertEquals(2.0, movesWithScore.get(Integer.valueOf(1)).score);
        assertEquals(0.5, movesWithScore.get(Integer.valueOf(2)).score);
        assertEquals(1.0, movesWithScore.get(Integer.valueOf(3)).score);
        assertEquals(1.0, movesWithScore.get(Integer.valueOf(4)).score);
        AnalyzedMove<Integer> bestMove = mergedRestult.getBestMove(mergedRestult.getPlayer());
        assertEquals(2.0, bestMove.analysis.score);
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
        assertTrue(AnalysisResult.isGreater(2, 1), "2 > 1");
        assertFalse(AnalysisResult.isGreater(1, 2), "1 > 2");
        assertFalse(AnalysisResult.isGreater(0, 0), "0 > 0");
        assertTrue(AnalysisResult.isGreater(0, AnalysisResult.DRAW), "0 > Draw");
        assertTrue(AnalysisResult.isGreater(0, AnalysisResult.DRAW), "1 > Draw");
        assertFalse(AnalysisResult.isGreater(-1, AnalysisResult.DRAW), "-1 > Draw");
        assertTrue(AnalysisResult.isGreater(AnalysisResult.DRAW, -1), "Draw > -1");
        assertFalse(AnalysisResult.isGreater(AnalysisResult.DRAW, 0), "Draw > 0");
        assertFalse(AnalysisResult.isGreater(AnalysisResult.DRAW, 1), "Draw > 1");
        assertFalse(AnalysisResult.isGreater(AnalysisResult.DRAW, AnalysisResult.DRAW), "Draw > Draw");
    }
}
