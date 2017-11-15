package game.ultimatetictactoe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;

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
		for (int i = 0; i < UltimateTicTacToePosition.BOARD_WIDTH; ++i) {
			if ((position.wonBoards & (TwoPlayers.BOTH_PLAYERS << (i << 2))) != TwoPlayers.UNPLAYED) {
				containsWonBoard = true;
			}
		}
		assertTrue(TicTacToeUtilities.boardToString(position.wonBoards), containsWonBoard);
	}

	private static void checkEqual(UltimateTicTacToePosition expected, UltimateTicTacToePosition actual, int moveNum) {
		assertEquals("Move " + moveNum, expected.currentPlayer, actual.currentPlayer);
		assertEquals("Move " + moveNum, expected.currentBoard, actual.currentBoard);
		assertEquals("Move " + moveNum, TicTacToeUtilities.boardToString(expected.wonBoards), TicTacToeUtilities.boardToString(actual.wonBoards));
		assertEquals("Move " + moveNum, expected.toString(), actual.toString());
	}

	@Test
	public void testCannotMoveIntoFullBoard() {
		UltimateTicTacToePosition position = new UltimateTicTacToePosition();
		position.makeMove(new UTTTCoordinate(4, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 0, position.currentBoard));
		position.makeMove(new UTTTCoordinate(0, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 1, position.currentBoard));
		position.makeMove(new UTTTCoordinate(1, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 7, position.currentBoard));
		position.makeMove(new UTTTCoordinate(7, 2, position.currentBoard));
		position.makeMove(new UTTTCoordinate(2, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 2, position.currentBoard));
		position.makeMove(new UTTTCoordinate(2, 3, position.currentBoard));
		position.makeMove(new UTTTCoordinate(3, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 6, position.currentBoard));
		position.makeMove(new UTTTCoordinate(6, 5, position.currentBoard));
		position.makeMove(new UTTTCoordinate(5, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 8, position.currentBoard));
		position.makeMove(new UTTTCoordinate(8, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 3, position.currentBoard));
		position.makeMove(new UTTTCoordinate(3, 7, position.currentBoard));
		position.makeMove(new UTTTCoordinate(7, 4, position.currentBoard));
		position.makeMove(new UTTTCoordinate(4, 5, position.currentBoard));
		position.makeMove(new UTTTCoordinate(5, 6, position.currentBoard));
		position.makeMove(new UTTTCoordinate(6, 4, position.currentBoard));
		assertEquals(UltimateTicTacToePosition.ANY_BOARD, position.currentBoard);
	}
}
