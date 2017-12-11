package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import util.Pair;

public class AnalysisResultTest {
	@Test
	public void testMerged() {
		AnalysisResult<Integer> result1 = createResult(Arrays.asList(Pair.valueOf(1, 1.0), Pair.valueOf(2, 1.0), Pair.valueOf(3, 1.0), Pair.valueOf(4, 1.0)));
		AnalysisResult<Integer> result2 = createResult(Arrays.asList(Pair.valueOf(1, 2.0), Pair.valueOf(2, 0.5)));
		AnalysisResult<Integer> mergedRestult = result1.mergeWith(result2);
		assertEquals(4, result1.getMovesWithScore().size());
		assertTrue(mergedRestult.getMovesWithScore().contains(new MoveWithScore<>(Integer.valueOf(1), 2.0)));
		assertTrue(mergedRestult.getMovesWithScore().contains(new MoveWithScore<>(Integer.valueOf(2), 0.5)));
		assertTrue(mergedRestult.getMovesWithScore().contains(new MoveWithScore<>(Integer.valueOf(3), 1.0)));
		assertTrue(mergedRestult.getMovesWithScore().contains(new MoveWithScore<>(Integer.valueOf(4), 1.0)));
		assertEquals(2.0, mergedRestult.getMax().score, 0.001);
		assertEquals(Integer.valueOf(1), mergedRestult.getBestMove());
	}

	private static AnalysisResult<Integer> createResult(List<Pair<Integer, Double>> movesWithScore) {
		AnalysisResult<Integer> result = new AnalysisResult<>();
		for (Pair<Integer, Double> moveWithScore : movesWithScore) {
			result.addMoveWithScore(moveWithScore.getFirst(), moveWithScore.getSecond().doubleValue());
		}
		return result;
	}

	@Test
	public void testFindBestMoveEvenIfLost() {
		AnalysisResult<String> result = new AnalysisResult<>();
		result.addMoveWithScore("1", Double.NEGATIVE_INFINITY);
		assertEquals("1", result.getBestMove());
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
