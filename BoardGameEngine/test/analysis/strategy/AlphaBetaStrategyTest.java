package analysis.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.Coordinate;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class AlphaBetaStrategyTest {
	@Test
	public void testAlphaBetaEqualsMinMax() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();

		MinimaxStrategy<Coordinate, UltimateTicTacToePosition> minmaxStrategy = new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator());
		AlphaBetaStrategy<Coordinate, UltimateTicTacToePosition> alphabetaStrategy = new AlphaBetaStrategy<>(new UltimateTicTacToePositionEvaluator());

		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> minmaxSearcher = new IterativeDeepeningTreeSearcher<>(minmaxStrategy, 4);
		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> alphabetaSearcher = new IterativeDeepeningTreeSearcher<>(alphabetaStrategy, 4);

		for (int plies = 0; plies < 7; ++plies) {
			long start1 = System.currentTimeMillis();
			AnalysisResult<Coordinate> minmaxResult = minmaxSearcher.startSearch(position, plies);
			long minmaxTime = System.currentTimeMillis() - start1;
			long start2 = System.currentTimeMillis();
			AnalysisResult<Coordinate> alphaBetaResult = alphabetaSearcher.startSearch(position, plies);
			long alphaBetaTime = System.currentTimeMillis() - start2;
			System.out.println("MM: " + minmaxTime + "ms, AB: " + alphaBetaTime + "ms, depth: " + plies + ", " + minmaxResult.getBestMove() + ": " + minmaxResult.getMax());
			assertEquals("Comparing score " + plies, minmaxResult.getMax(), alphaBetaResult.getMax(), 0.000000001);
		}
	}
}
