package game.ultimatetictactoe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import game.ArrayMoveList;
import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;

public class UltimateTicTacToePositionEvaluatorTest {
	@Test
	public void testEvaluationsEqualButOpposite() {
		UltimateTicTacToePositionEvaluator evaluator = new UltimateTicTacToePositionEvaluator();
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		for (int i = 0; i < 20; ++i) {
			MoveList<Coordinate> possibleMoves = new ArrayMoveList<>(UltimateTicTacToeGame.MAX_MOVES);
			position.getPossibleMoves(possibleMoves);
			assertEquals(evaluator.evaluate(position, possibleMoves, TwoPlayers.PLAYER_1), -evaluator.evaluate(position, possibleMoves, TwoPlayers.PLAYER_2), 0.001);
			position.makeMove(possibleMoves.get(0));
		}
	}
}
