package analysis.montecarlo;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import analysis.IPositionEvaluator;
import game.IPosition;
import game.MoveListFactory;
import game.chess.ChessGame;
import game.chess.ChessPosition;
import game.chess.ChessPositionEvaluator;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToePosition;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;

public class MonteCarloGameNodeTest {
	@Test
	public void testMonteCarlo_UTTT() {
		doSearch(new UltimateTicTacToePosition(), new UltimateTicTacToePositionEvaluator(), UltimateTicTacToeGame.MAX_MOVES);
	}

	@Test
	public void testMonteCarlo_Chess() {
		doSearch(new ChessPosition(), new ChessPositionEvaluator(), ChessGame.MAX_MOVES);
	}

	private static <M, P extends IPosition<M>> void doSearch(P position, IPositionEvaluator<M, P> positionEvaluator, int maxMoves) {
		MonteCarloTreeSearcher<M, P> treeSearcher = new MonteCarloTreeSearcher<>(positionEvaluator, new MoveListFactory<>(maxMoves), 25);
		treeSearcher.searchForever(position, false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		treeSearcher.stopSearch(true);

		MonteCarloGameNode<M, P> root = treeSearcher.getRoot();

		int totalPositionsEvaluated = root.statistics.nodesEvaluated;
		System.out.println("Positions evaluated: " + totalPositionsEvaluated);

		ArrayList<MonteCarloGameNode<M, P>> sortedChildren = new ArrayList<>(root.expandedChildren);
		Collections.sort(sortedChildren, (c1, c2) -> Double.compare(c2.statistics.getExpectedValue(totalPositionsEvaluated), c1.statistics.getExpectedValue(totalPositionsEvaluated)));

		for (MonteCarloGameNode<M, P> child : sortedChildren) {
			System.out.println(child.parentMove + " -> " + child.statistics.toString(totalPositionsEvaluated));
		}
	}
}
