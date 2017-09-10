package game.ultimatetictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import game.Coordinate;
import game.TwoPlayers;

public class UltimateTicTacToePositionTest {
	@Test
	public void testUnmakeManyMoves() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		List<UTTTCoordinate> moves = new ArrayList<>();
		List<UltimateTicTacToePosition> positions = new ArrayList<>();
		for (int i = 0; i < 20; ++i) {
			positions.add(position.createCopy());
			checkEqual(position, positions.get(i), -1);
			UTTTCoordinate move = position.getPossibleMoves().get(0);
			moves.add(move);
			position.makeMove(move);
		}
		checkContainsWonBoard(position);
		Collections.reverse(moves);
		Collections.reverse(positions);
		for (int i = 0; i < 20; ++i) {
			position.unmakeMove(moves.get(i));
			checkEqual(positions.get(i), position, i);
		}
	}

	private static void checkContainsWonBoard(UltimateTicTacToePosition position) {
		boolean containsWonBoard = false;
		for (int i = 0; i < position.wonBoards.length; i++) {
			if (position.wonBoards[i] != TwoPlayers.UNPLAYED) {
				containsWonBoard = true;
			}
		}
		assertTrue(containsWonBoard);
	}

	private static void checkEqual(UltimateTicTacToePosition expected, UltimateTicTacToePosition actual, int moveNum) {
		assertEquals("Move " + moveNum, expected.currentPlayer, actual.currentPlayer);
		assertEquals("Move " + moveNum, expected.currentBoard, actual.currentBoard);
		assertEquals("Move " + moveNum, Arrays.toString(expected.wonBoards), Arrays.toString(actual.wonBoards));
		assertEquals("Move " + moveNum, expected.toString(), actual.toString());
	}

	@Test
	public void testCannotMoveIntoFullBoard() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(0, 0), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(1, 0), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(3, 0), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(2, 0), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(6, 0), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(0, 1), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(0, 3), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(1, 1), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(4, 4), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(3, 3), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(2, 1), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(6, 3), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(0, 2), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(0, 6), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(1, 2), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(4, 8), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(3, 6), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(2, 2), position.currentBoard));
		position.makeMove(new UTTTCoordinate(Coordinate.valueOf(6, 6), position.currentBoard));
		assertEquals(UltimateTicTacToePosition.ANY_BOARD, position.currentBoard);
	}
}
