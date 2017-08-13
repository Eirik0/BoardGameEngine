package analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import util.Pair;

public class AnalysisRestutTest {
	@Test
	public void testMerged() {
		AnalysisResult<Integer> result1 = createResult(Arrays.asList(Pair.valueOf(1, 1.0), Pair.valueOf(2, 1.0), Pair.valueOf(3, 1.0), Pair.valueOf(4, 1.0)));
		AnalysisResult<Integer> result2 = createResult(Arrays.asList(Pair.valueOf(1, 2.0), Pair.valueOf(2, 0.5)));
		AnalysisResult<Integer> mergedRestult = result1.mergeWith(result2);
		assertEquals(4, result1.getMovesWithScore().size());
		assertTrue(mergedRestult.getMovesWithScore().contains(Pair.valueOf(1, 2.0)));
		assertTrue(mergedRestult.getMovesWithScore().contains(Pair.valueOf(2, 0.5)));
		assertTrue(mergedRestult.getMovesWithScore().contains(Pair.valueOf(3, 1.0)));
		assertTrue(mergedRestult.getMovesWithScore().contains(Pair.valueOf(4, 1.0)));
		assertEquals(2.0, mergedRestult.getMax(), 0.001);
		assertEquals(0.5, mergedRestult.getMin(), 0.001);
		assertEquals(Integer.valueOf(1), mergedRestult.getBestMove());
	}

	private static AnalysisResult<Integer> createResult(List<Pair<Integer, Double>> movesWithScore) {
		AnalysisResult<Integer> result = new AnalysisResult<>();
		for (Pair<Integer, Double> moveWithScore : movesWithScore) {
			result.addMoveWithScore(moveWithScore.getFirst(), moveWithScore.getSecond());
		}
		return result;
	}
}
