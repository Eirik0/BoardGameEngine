package analysis.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.ultimatetictactoe.UTTTCoordinate;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class AlphaBetaStrategyTest {
	@Test
	public void testAlphaBetaEqualsMinMax() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();

		MinimaxStrategy<UTTTCoordinate, UltimateTicTacToePosition> minmaxStrategy = new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator());
		AlphaBetaStrategy<UTTTCoordinate, UltimateTicTacToePosition> alphabetaStrategy = new AlphaBetaStrategy<>(new UltimateTicTacToePositionEvaluator());

		IterativeDeepeningTreeSearcher<UTTTCoordinate, UltimateTicTacToePosition> minmaxSearcher = new IterativeDeepeningTreeSearcher<>(minmaxStrategy, 4);
		IterativeDeepeningTreeSearcher<UTTTCoordinate, UltimateTicTacToePosition> alphabetaSearcher = new IterativeDeepeningTreeSearcher<>(alphabetaStrategy, 4);

		for (int plies = 0; plies < 7; ++plies) {
			long start1 = System.currentTimeMillis();
			AnalysisResult<UTTTCoordinate> minmaxResult = minmaxSearcher.startSearch(position, plies);
			long minmaxTime = System.currentTimeMillis() - start1;
			long start2 = System.currentTimeMillis();
			AnalysisResult<UTTTCoordinate> alphaBetaResult = alphabetaSearcher.startSearch(position, plies);
			long alphaBetaTime = System.currentTimeMillis() - start2;
			assertEquals("Comparing score " + plies, minmaxResult.getMax(), alphaBetaResult.getMax(), 0.000000001);
			assertEquals(minmaxResult.getBestMove(), alphaBetaResult.getBestMove());
			System.out.println("MM: " + minmaxTime + "ms, AB: " + alphaBetaTime + "ms, depth: " + plies + ", " + minmaxResult.getBestMove() + ": " + minmaxResult.getMax());
		}
	}
}
