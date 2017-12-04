package game.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import analysis.AnalysisResult;
import analysis.MoveWithScore;
import analysis.search.IterativeDeepeningTreeSearcher;
import analysis.strategy.MinimaxStrategy;
import game.Coordinate;
import game.MoveListFactory;

public class TicTacToeTreeSearchTest {
	private static IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher() {
		return newTreeSearcher(1);
	}

	private static IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> newTreeSearcher(int numWorkers) {
		MoveListFactory<Coordinate> moveListFactory = new MoveListFactory<>(TicTacToeGame.MAX_MOVES);
		return new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(moveListFactory, new TicTacToePositionEvaluator()), moveListFactory, numWorkers);
	}

	@Test
	public void testSearchTicTacToe() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 1, true);
		assertEquals(Coordinate.valueOf(0, 0), search.getBestMove());
		treeSearcher.stopSearch(true);
	}

	@Test
	public void testSearchTicTacToe_TestSearchTwoPlies() throws InterruptedException {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> result = treeSearcher.startSearch(position, 2, true);
		treeSearcher.stopSearch(true);
		assertEquals(Coordinate.valueOf(0, 0), result.getBestMove());
	}

	@Test
	public void testSearchTicTacToe_TestPlayGame() throws InterruptedException {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[X  ],[   ],[   ]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[XO ],[   ],[   ]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[XOX],[   ],[   ]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[XOX],[O  ],[   ]", position.toString());
		searchAndMove(treeSearcher, position, 2);
		assertEquals("[XOX],[OX ],[   ]", position.toString());
		treeSearcher.stopSearch(true);
	}

	private static void searchAndMove(IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher, TicTacToePosition position, int plies) {
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, plies, true);
		position.makeMove(search.getBestMove());
	}

	@Test
	public void testFullSearch() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9, true);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(AnalysisResult.DRAW, moveWithScore.score, 0.0);
		}
		treeSearcher.stopSearch(true);
	}

	@Test
	public void testFindWin() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 1));
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher();
		AnalysisResult<Coordinate> search = treeSearcher.startSearch(position, 9, true);
		Coordinate draw = Coordinate.valueOf(2, 1);
		for (MoveWithScore<Coordinate> moveWithScore : search.getMovesWithScore()) {
			assertEquals(moveWithScore.move.toString(), moveWithScore.move.equals(draw) ? 0.0 : Double.POSITIVE_INFINITY, moveWithScore.score, 0.0);
		}
		treeSearcher.stopSearch(true);
	}

	@Test
	public void testStopSearchIfAllPositionsEvaluated() {
		TicTacToePosition position = new TicTacToePosition();
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher(2);
		treeSearcher.startSearch(position, 9, true);
		assertEquals(9, treeSearcher.getPlies());
		treeSearcher.startSearch(position, 10, true);
		assertEquals(9, treeSearcher.getPlies());
		treeSearcher.startSearch(position, 11, true);
		assertEquals(9, treeSearcher.getPlies());
		treeSearcher.stopSearch(true);
	}

	@Test
	public void testDefendAgainstLoss() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(0, 0));
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(2, 0));
		position.makeMove(Coordinate.valueOf(1, 0));
		position.makeMove(Coordinate.valueOf(1, 2));
		position.makeMove(Coordinate.valueOf(0, 1));
		position.makeMove(Coordinate.valueOf(2, 1));
		IterativeDeepeningTreeSearcher<Coordinate, TicTacToePosition> treeSearcher = newTreeSearcher(2);
		for (int i = 0; i < 1000; ++i) {
			AnalysisResult<Coordinate> result = treeSearcher.startSearch(position, 11, true);
			assertEquals("search " + String.valueOf(i), AnalysisResult.DRAW, result.getMax().score, 0.0);
			assertTrue("search " + String.valueOf(i), result.isDraw());
		}
		treeSearcher.stopSearch(true);
	}
}
