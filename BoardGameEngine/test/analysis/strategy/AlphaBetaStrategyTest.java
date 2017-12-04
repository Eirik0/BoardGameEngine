package analysis.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.Coordinate;
import game.IPosition;
import game.MoveListFactory;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class AlphaBetaStrategyTest {
	@Test
	public void testAlphaBetaEqualsMinMax() {
		MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(UltimateTicTacToeGame.MAX_MOVES);

		MinimaxStrategy<Coordinate, UltimateTicTacToePosition> minmaxStrategy = new MinimaxStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());
		AlphaBetaStrategy<Coordinate, UltimateTicTacToePosition> alphabetaStrategy = new AlphaBetaStrategy<>(moveListFactory, new UltimateTicTacToePositionEvaluator());

		compareStrategies(new UltimateTicTacToePosition(), moveListFactory, minmaxStrategy, alphabetaStrategy, 4, 5);
	}

	public static <M, P extends IPosition<M, P>> void compareStrategies(P position, MoveListFactory<M> moveListFactory, IDepthBasedStrategy<M, P> strat1, IDepthBasedStrategy<M, P> strat2,
			int numThreads, int maxPlies) {
		IterativeDeepeningTreeSearcher<M, P> s1 = new IterativeDeepeningTreeSearcher<>(strat1, moveListFactory, numThreads);
		IterativeDeepeningTreeSearcher<M, P> s2 = new IterativeDeepeningTreeSearcher<>(strat2, moveListFactory, numThreads);
		System.out.println("S1: " + strat1.getClass().getSimpleName() + ", S2: " + strat2.getClass().getSimpleName());
		int plies = 0;
		do {
			long start1 = System.currentTimeMillis();
			AnalysisResult<M> s1Result = s1.startSearch(position, plies, true);
			long s1Time = System.currentTimeMillis() - start1;
			long start2 = System.currentTimeMillis();
			AnalysisResult<M> s2Result = s2.startSearch(position, plies, true);
			long s2Time = System.currentTimeMillis() - start2;
			System.out.println("S1: " + s1Time + "ms, S2: " + s2Time + "ms, depth: " + plies + ", " + s1Result.getBestMove() + ": " + s1Result.getMax());
			assertEquals("Comparing score " + plies, s1Result.getMax().score, s2Result.getMax().score, 0.001);
			Map<M, Double> s2MoveMap = createMoveMap(s2Result);

			for (MoveWithScore<M> moveWithScore : s1Result.getMovesWithScore()) {
				if (!(Math.abs(moveWithScore.score - s2MoveMap.get(moveWithScore.move)) < 0.001)) {
					printResults(s1Result, s2Result);
					fail(moveWithScore + " " + s2MoveMap.get(moveWithScore.move));
				}
			}
			++plies;
		} while (plies <= maxPlies);

		s1.stopSearch(true);
		s2.stopSearch(true);
	}

	private static <M> Map<M, Double> createMoveMap(AnalysisResult<M> result) {
		Map<M, Double> moveMap = new HashMap<>();
		for (MoveWithScore<M> moveWithScore : result.getMovesWithScore()) {
			moveMap.put(moveWithScore.move, moveWithScore.score);
		}
		return moveMap;
	}

	private static <M> void printResults(AnalysisResult<M> s1Result, AnalysisResult<M> s2Result) {
		List<MoveWithScore<M>> s1MovesWithScore = s1Result.getMovesWithScore();
		List<MoveWithScore<M>> s2MovesWithScore = s2Result.getMovesWithScore();
		Collections.sort(s1MovesWithScore, (ms1, ms2) -> Double.compare(ms1.score, ms2.score));
		Collections.sort(s2MovesWithScore, (ms1, ms2) -> Double.compare(ms1.score, ms2.score));
		for (int i = 0; i < s1MovesWithScore.size(); ++i) {
			MoveWithScore<M> s1MoveWithScore = s1MovesWithScore.get(i);
			MoveWithScore<M> s2MoveWithScore = s2MovesWithScore.get(i);
			System.out.println(s1MoveWithScore + " " + s2MoveWithScore);
		}
	}
}
