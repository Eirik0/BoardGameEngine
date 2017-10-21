package game.chess;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import game.chess.fen.ForsythEdwardsNotation;
import game.chess.move.IChessMove;

public class ChessPositionTest implements ChessConstants {
	@Test
	public void testMovesFromInitialPosition() {
		ChessPosition position = new ChessPosition();
		assertEquals(20, position.getPossibleMoves().size());
	}

	public static void assertPositionsEqual(ChessPosition expected, ChessPosition actual) {
		assertEquals(getBoardStr(expected), getBoardStr(actual));
		assertEquals("Current player", expected.currentPlayer, actual.currentPlayer);
		assertEquals("Castle state", expected.castleState, actual.castleState);
		assertEquals("En passant square", expected.enPassantSquare, actual.enPassantSquare);
		assertEquals("White king square", expected.whiteKingSquare, actual.whiteKingSquare);
		assertEquals("Black king square", expected.blackKingSquare, actual.blackKingSquare);
		assertEquals("Half move clock", expected.halfMoveClock, actual.halfMoveClock);
		assertEquals("Ply count", expected.plyCount, actual.plyCount);
	}

	private static String getBoardStr(ChessPosition expected) {
		return Arrays.stream(expected.squares)
				.map(row -> Arrays.stream(row).mapToObj(piece -> ForsythEdwardsNotation.getPieceString(piece)).collect(Collectors.joining(" ")))
				.collect(Collectors.joining("\n"));
	}

	public static void makeMove(ChessPosition position, String moveString) {
		position.makeMove(getMove(position.getPossibleMoves(), moveString));
	}

	private static IChessMove getMove(List<IChessMove> possibleMoves, String moveString) {
		for (IChessMove chessMove : possibleMoves) {
			if (chessMove.toString().equals(moveString)) {
				return chessMove;
			}
		}
		throw new IllegalStateException("Move not found: " + moveString);
	}
}
