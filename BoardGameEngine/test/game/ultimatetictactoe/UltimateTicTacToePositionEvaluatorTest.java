package game.ultimatetictactoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UltimateTicTacToePositionEvaluatorTest {
	@Test
	public void testEvaluationsEqualButOpposite() {
		UltimateTicTacToePositionEvaluator evaluator = new UltimateTicTacToePositionEvaluator();
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		for (int i = 0; i < 20; ++i) {
			assertEquals(evaluator.evaluate(position, 1), -evaluator.evaluate(position, 2), 0.001);
			position.makeMove(position.getPossibleMoves().get(0));
		}
	}
}
