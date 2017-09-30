package game.chess;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ChessPositionTest {
	@Test
	public void testMovesFromInitialPosition() {
		ChessPosition position = new ChessPosition();
		assertEquals(20, position.getPossibleMoves().size());
	}
}
