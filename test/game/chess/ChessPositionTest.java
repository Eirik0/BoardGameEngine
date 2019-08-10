package game.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import game.ArrayMoveList;
import game.MoveList;
import game.TwoPlayers;
import game.chess.ChessPositionHistory.UndoChessMove;
import game.chess.fen.ForsythEdwardsNotation;
import game.chess.move.IChessMove;

public class ChessPositionTest implements ChessConstants {
    @Test
    public void testMovesFromInitialPosition() {
        ChessPosition position = new ChessPosition();
        MoveList<IChessMove> possibleMoves = new ArrayMoveList<>(ChessGame.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        assertEquals(20, possibleMoves.size());
    }

    public List<String> movesToStrings(MoveList<IChessMove> moveList) {
        List<String> stringList = new ArrayList<>();
        int i = 0;
        while (i < moveList.size()) {
            stringList.add(moveList.get(i).toString());
            ++i;
        }
        return stringList;
    }

    @Test
    public void testGetKnightMoves() {
        ChessPosition position = ForsythEdwardsNotation.stringToPosition("8/1n4N1/2k5/8/8/5K2/1N4n1/8 b - - 0 1");
        MoveList<IChessMove> possibleMoves = new ArrayMoveList<>(ChessGame.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        assertEquals(15, possibleMoves.size());
        List<String> expectedMoves = Arrays.asList("g2-e1", "g2-e3", "g2-f4", "g2-h4", "c6-d5", "c6-c5", "c6-b5", "c6-b6", "c6-c7", "c6-d7", "c6-d6", "b7-c5",
                "b7-a5", "b7-d8", "b7-d6");
        List<String> moveStrings = movesToStrings(possibleMoves);
        moveStrings.retainAll(expectedMoves);
        assertEquals(15, moveStrings.size());
    }

    @Test
    public void testCantCastleThroughCheck() {
        ChessPosition position = ForsythEdwardsNotation.stringToPosition("8/8/8/8/8/8/6k1/4K2R w K - 0 1");
        MoveList<IChessMove> possibleMoves = new ArrayMoveList<>(ChessGame.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        assertEquals(12, possibleMoves.size());
        List<String> expectedMoves = Arrays.asList("h1-g1", "h1-f1", "h1-h2", "h1-h3", "h1-h4", "h1-h5", "h1-h6", "h1-h7", "h1-h8", "e1-d1", "e1-e2", "e1-d2");
        List<String> moveStrings = movesToStrings(possibleMoves);
        moveStrings.retainAll(expectedMoves);
        assertEquals(12, moveStrings.size());
    }

    @Test
    public void testPromotePawn() {
        ChessPosition position = ForsythEdwardsNotation.stringToPosition("8/Pk6/8/8/8/8/6Kp/8 w - - 0 1");
        int pawnsBefore = position.numPawns[TwoPlayers.PLAYER_1];
        int queensBefore = position.numQueens[TwoPlayers.PLAYER_1];
        ChessPositionTest.makeMove(position, "a7-a8q");
        assertEquals(pawnsBefore - 1, position.numPawns[TwoPlayers.PLAYER_1]);
        assertEquals(queensBefore + 1, position.numQueens[TwoPlayers.PLAYER_1]);
    }

    public static void assertPositionIntegrity(ChessPosition position) {
        int whiteKingSquare = position.kingSquares[TwoPlayers.PLAYER_1];
        int blackKingSquare = position.kingSquares[TwoPlayers.PLAYER_2];
        assertEquals("White king square", WHITE_KING, position.squares[whiteKingSquare]);
        assertEquals("Black king square", BLACK_KING, position.squares[blackKingSquare]);
        double[] expectedMaterialScore = ForsythEdwardsNotation.getMaterialScore(position.squares);
        assertEquals("White material score", expectedMaterialScore[1], position.materialScore[1], 0.01);
        assertEquals("Black material score", expectedMaterialScore[2], position.materialScore[2], 0.01);
    }

    public static void assertPositionsEqual(ChessPosition expected, ChessPosition actual) {
        assertEquals(ChessFunctions.getBoardStr(expected), ChessFunctions.getBoardStr(actual));
        assertEquals("Current player", expected.currentPlayer, actual.currentPlayer);
        assertEquals("Castle state", expected.castleState, actual.castleState);
        assertEquals("En passant square", expected.enPassantSquare, actual.enPassantSquare);
        assertEquals("White king square", expected.kingSquares[1], actual.kingSquares[1]);
        assertEquals("Black king square", expected.kingSquares[2], actual.kingSquares[2]);
        assertEquals("White material score", expected.materialScore[1], actual.materialScore[1], 0.01);
        assertEquals("Black material score", expected.materialScore[2], actual.materialScore[2], 0.01);
        assertEquals("Half move clock", expected.halfMoveClock, actual.halfMoveClock);
        assertEquals("Ply count", expected.positionHistory.plyCount, actual.positionHistory.plyCount);
        for (int i = 0; i < expected.positionHistory.plyCount; ++i) {
            UndoChessMove expectedUndoMove = expected.positionHistory.undoChessMoves[i];
            UndoChessMove actualUndoMove = actual.positionHistory.undoChessMoves[i];
            if (expectedUndoMove == null) {
                assertNull(actualUndoMove);
                continue;
            }
            assertEquals("Castle state " + i, expectedUndoMove.priorCastleState, actualUndoMove.priorCastleState);
            assertEquals("En passant Square " + i, expectedUndoMove.priorEnPassantSquare, actualUndoMove.priorEnPassantSquare);
            assertEquals("Half move clock " + i, expectedUndoMove.priorHalfMoveClock, actualUndoMove.priorHalfMoveClock);
        }
    }

    public static void makeMove(ChessPosition position, String moveString) {
        MoveList<IChessMove> possibleMoves = new ArrayMoveList<>(ChessGame.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        position.makeMove(getMove(possibleMoves, moveString));
    }

    private static IChessMove getMove(MoveList<IChessMove> possibleMoves, String moveString) {
        int i = 0;
        while (i < possibleMoves.size()) {
            if (possibleMoves.get(i).toString().equals(moveString)) {
                return possibleMoves.get(i);
            }
            ++i;
        }
        throw new IllegalStateException("Move not found: " + moveString);
    }
}
