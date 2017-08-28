package game.tictactoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MinimaxStrategy;
import analysis.MoveWithScore;
import analysis.search.IterativeDeepeningTreeSearcher;
import game.Coordinate;

public class TicTacToeTreeSearchTest {
	private static IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher() {
		return new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(new TicTacToePositionEvaluator()), 1);
	}

	@Test
	public void testSearchTicTacToe() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.search(position, 1, 1);
		assertEquals(new Coordinate(0, 0), search.getBestMove());
	}

	@Test
	public void testSearchTicTacToe_TestSearchTwoPlies() throws InterruptedException {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> result = search(treeSearcher, position, 2);
		assertEquals(new Coordinate(0, 0), result.getBestMove());
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
		AnalysisResult<Coordinate> search = search(treeSearcher, position, plies);
		position.makeMove(search.getBestMove());
	}

	private static AnalysisResult<Coordinate> search(IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher, TicTacToePosition position, int plies) {
		treeSearcher.startSearch(position, plies);
		return treeSearcher.getResult();
	}

	@Test
	public void testFullSearch() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = search(treeSearcher, position, 9);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(0.0, moveWithScore.score, 0.0);
		}
	}

	@Test
	public void testFindWin() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(0, 1));
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = search(treeSearcher, position, 9);
		Coordinate draw = new Coordinate(2, 1);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(moveWithScore.move.toString(), moveWithScore.move.equals(draw) ? 0.0 : Double.POSITIVE_INFINITY, moveWithScore.score, 0.0);
		}
	}
}
