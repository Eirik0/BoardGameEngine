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
		position.makeMove(Coordinate.valueOf(1, 1));
		assertEquals("[   ],[ X ],[   ]", position.toString());
		assertEquals(2, position.getCurrentPlayer());
	}

	@Test
	public void testMakeTwoMoves() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 2));
		assertEquals("[   ],[ X ],[O  ]", position.toString());
		assertEquals(1, position.getCurrentPlayer());
	}

	@Test
	public void testMakeTwoMoves_UnmakeSecond() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 2));
		position.unmakeMove(Coordinate.valueOf(0, 2));
		assertEquals("[   ],[ X ],[   ]", position.toString());
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
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 2));
		List<Coordinate> moves = position.getPossibleMoves();
		assertEquals(7, moves.size());
		assertFalse(moves.contains(Coordinate.valueOf(1, 1)));
		assertFalse(moves.contains(Coordinate.valueOf(0, 2)));
	}

	@Test
	public void testCopyBoard() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(0, 2));
		position.makeMove(Coordinate.valueOf(2, 2));
		assertEquals(position.toString(), position.createCopy().toString());
	}

	@Test
	public void testGameEnds() {
		TicTacToePosition position = new TicTacToePosition();
		position.makeMove(Coordinate.valueOf(1, 0));
		position.makeMove(Coordinate.valueOf(2, 0));
		position.makeMove(Coordinate.valueOf(1, 1));
		position.makeMove(Coordinate.valueOf(2, 1));
		position.makeMove(Coordinate.valueOf(1, 2));
		assertEquals(0, position.getPossibleMoves().size());
	}

	@Test
	public void testWinsExist() {
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 1, 1, 1 }, { 0, 0, 0 }, { 0, 0, 0 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 0, 0, 0 }, { 1, 1, 1 }, { 0, 0, 0 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 1, 1, 1 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 1, 0, 0 }, { 1, 0, 0 }, { 1, 0, 0 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 0, 1, 0 }, { 0, 1, 0 }, { 0, 1, 0 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 0, 0, 1 }, { 0, 0, 1 }, { 0, 0, 1 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } }), 1));
		assertTrue(TicTacToeUtilities.winsExist(boardToInt(new int[][] { { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 } }), 1));
	}

	private static int boardToInt(int[][] board) {
		int pos0 = board[0][0];
		int pos1 = board[0][1];
		int pos2 = board[0][2];
		int pos3 = board[1][0];
		int pos4 = board[1][1];
		int pos5 = board[1][2];
		int pos6 = board[2][0];
		int pos7 = board[2][1];
		int pos8 = board[2][2];
		return (pos0 << 0) | (pos1 << 2) | (pos2 << 4) | (pos3 << 6) | (pos4 << 8) | (pos5 << 10) | (pos6 << 12) | (pos7 << 14) | (pos8 << 16);
	}
}
