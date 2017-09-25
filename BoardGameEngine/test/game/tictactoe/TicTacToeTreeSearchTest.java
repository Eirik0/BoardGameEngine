package game.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.search.IterativeDeepeningTreeSearcher;
import analysis.strategy.MinimaxStrategy;
import game.Coordinate;

public class TicTacToeTreeSearchTest {
	private static IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher() {
		return newTreeSearcher(1);
	}

	private static IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher(int numWorkers) {
		return new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(new TicTacToePositionEvaluator()), numWorkers);
	}

	@Test
	public void testSearchTicTacToe() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 1);
		assertEquals(Coordinate.valueOf(0, 0), search.getBestMove());
	}

	@Test
	public void testSearchTicTacToe_TestSearchTwoPlies() throws InterruptedException {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> result = treeSearcher.startSearch(position, 2);
		assertEquals(Coordinate.valueOf(0, 0), result.getBestMove());
	}

	@Test
	public void testSearchTicTacToe_TestPlayGame() throws InterruptedException {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[[1, 0, 0], [0, 0, 0], [0, 0, 0]]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[[1, 2, 0], [0, 0, 0], [0, 0, 0]]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[[1, 2, 1], [0, 0, 0], [0, 0, 0]]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[[1, 2, 1], [2, 0, 0], [0, 0, 0]]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[[1, 2, 1], [2, 1, 0], [0, 0, 0]]", position.toString());
	}

	private static void searchAndMove(IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher, TicTacToePosition position, int plies) {
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, plies);
		position.makeMove(search.getBestMove());
	}

	@Test
	public void testFullSearch() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(0.0, moveWithScore.score, 0.0);
		}
	}

	@Test
	public void testFindWin() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 1));
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9);
		Coordinate draw = Coordinate.valueOf(2, 1);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(moveWithScore.move.toString(), moveWithScore.move.equals(draw) ? 0.0 : Double.POSITIVE_INFINITY, moveWithScore.score, 0.0);
		}
	}

	@Test
	public void testStopSearchIfAllPositionsEvaluated() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher(2);
		AnalysisResult<Coordinate> search1 = treeSearcher.startSearch(position, 9);
		assertFalse(search1.searchedAllPositions());
		AnalysisResult<Coordinate> search2 = treeSearcher.startSearch(position, 10);
		assertTrue(search2.searchedAllPositions());
		treeSearcher.startSearch(position, 11);
		assertEquals(10, treeSearcher.getPlies());
	}
}
