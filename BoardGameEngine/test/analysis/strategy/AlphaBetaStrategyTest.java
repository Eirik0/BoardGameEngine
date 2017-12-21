package analysis.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MoveAnalysis;
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

		compareStrategies(new UltimateTicTacToePosition(), moveListFactory, minmaxStrategy, alphabetaStrategy, 4, 6);
	}

	public static <M, P extends IPosition<M>> void compareStrategies(P position, MoveListFactory<M> moveListFactory, IDepthBasedStrategy<M, P> strat1, IDepthBasedStrategy<M, P> strat2,
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
			System.out.println("S1: " + s1Time + "ms, S2: " + s2Time + "ms, depth: " + plies + ", " + s1Result.getBestMove(true).toString());
			assertEquals("Comparing score " + plies, s1Result.getBestMove(true).analysis.score, s2Result.getBestMove(true).analysis.score, 0.001);
			Map<M, MoveAnalysis> s2MoveMap = s2Result.getMovesWithScore();
			for (Entry<M, MoveAnalysis> moveWithScore : s1Result.getMovesWithScore().entrySet()) {
				if (!(Math.abs(moveWithScore.getValue().score - s2MoveMap.get(moveWithScore.getKey()).score) < 0.001)) {
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
		Map<M, MoveAnalysis> s1MovesWithScore = s1Result.getMovesWithScore();
		Map<M, MoveAnalysis> s2MovesWithScore = s2Result.getMovesWithScore();
		ArrayList<M> moves = new ArrayList<>(s1MovesWithScore.keySet());
		moves.addAll(s2MovesWithScore.keySet());
		for (M move : moves) {
			System.out.println(move.toString() + " " + s1MovesWithScore.get(move).score + " " + s2MovesWithScore.get(move).score);
		}
	}
}
