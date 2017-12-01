package analysis.strategy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.Coordinate;
import game.MoveListFactory;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class AlphaBetaStrategyTest {
	@Test
	public void testAlphaBetaEqualsMinMax() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);
		MinimaxStrategy<Coordinate, UltimateTicTacToePosition> minmaxStrategy = new MinimaxStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());
		AlphaBetaStrategy<Coordinate, UltimateTicTacToePosition> alphabetaStrategy = new AlphaBetaStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());

		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> minmaxSearcher = new IterativeDeepeningTreeSearcher<>(minmaxStrategy,
				new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES), 4);
		IterativeDeepeningTreeSearcher<Coordinate, UltimateTicTacToePosition> alphabetaSearcher = new IterativeDeepeningTreeSearcher<>(alphabetaStrategy,
				new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES), 4);

		for (int plies = 0; plies < 7; ++plies) {
			long start1 = System.currentTimeMillis();
			AnalysisResult<Coordinate> minmaxResult = minmaxSearcher.startSearch(position, plies, true);
			long minmaxTime = System.currentTimeMillis() - start1;
			long start2 = System.currentTimeMillis();
			AnalysisResult<Coordinate> alphaBetaResult = alphabetaSearcher.startSearch(position, plies, true);
			long alphaBetaTime = System.currentTimeMillis() - start2;
			System.out.println("MM: " + minmaxTime + "ms, AB: " + alphaBetaTime + "ms, depth: " + plies + ", " + minmaxResult.getBestMove() + ": " + minmaxResult.getMax());
			assertEquals("Comparing score " + plies, minmaxResult.getMax().score, alphaBetaResult.getMax().score, 0.000000001);
		}
		minmaxSearcher.stopSearch(true);
		alphabetaSearcher.stopSearch(true);
	}
}
