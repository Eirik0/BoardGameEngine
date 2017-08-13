package game.tictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import game.Coordinate;

public class TicTacToePositionTest {
	@Test
	public void testMakeMove() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		assertEquals("[[0,0,0],[0,1,0],[0,0,0]]", position.toString().replaceAll("\\s+", ""));
		assertEquals(2, position.getCurrentPlayer());
	}

	@Test
	public void testMakeTwoMoves() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(0, 2));
		assertEquals("[[0,0,0],[0,1,0],[2,0,0]]", position.toString().replaceAll("\\s+", ""));
		assertEquals(1, position.getCurrentPlayer());
	}

	@Test
	public void testMakeTwoMoves_UnmakeSecond() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(0, 2));
		position.unmakeMove(new Coordinate(0, 2));
		assertEquals("[[0,0,0],[0,1,0],[0,0,0]]", position.toString().replaceAll("\\s+", ""));
		assertEquals(2, position.getCurrentPlayer());
	}

	@Test
	public void testGetPossibleMoves() {
		TicTacToePosition position = new TicTacToePosition();
		List<Coordinate> moves = position.getPossibleMoves();
		assertEquals(9, moves.size());
	}

	@Test
	public void testGetPossibleMoves_AfterTwoMade() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(0, 2));
		List<Coordinate> moves = position.getPossibleMoves();
		assertEquals(7, moves.size());
		assertFalse(moves.contains(new Coordinate(1, 1)));
		assertFalse(moves.contains(new Coordinate(0, 2)));
	}

	@Test
	public void testCopyBoard() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(0, 2));
		position.makeMove(new Coordinate(2, 2));
		assertEquals(position.toString(), position.createCopy().toString());
	}

	@Test
	public void testGameEnds() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(new Coordinate(1, 0));
		position.makeMove(new Coordinate(2, 0));
		position.makeMove(new Coordinate(1, 1));
		position.makeMove(new Coordinate(2, 1));
		position.makeMove(new Coordinate(1, 2));
		assertEquals(0, position.getPossibleMoves().size());
	}

	@Test
	public void testWinsExist() {
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 1, 1, 1 }, { 0, 0, 0 }, { 0, 0, 0 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 1, 1, 1 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 1, 0, 0 }, { 1, 0, 0 }, { 1, 0, 0 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 0, 1, 0 }, { 0, 1, 0 }, { 0, 1, 0 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } }, 1));
		assertTrue(TicTacToePosition.winsExist(new int[][] { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 } }, 1));
	}
}
