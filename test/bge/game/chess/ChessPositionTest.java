package bge.game.chess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import bge.game.chess.ChessPositionHistory.UndoChessMove;
import bge.game.chess.fen.ForsythEdwardsNotation;
import bge.game.chess.move.IChessMove;
import bge.igame.ArrayMoveList;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

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
        assertEquals(WHITE_KING, position.squares[whiteKingSquare], "White king square");
        assertEquals(BLACK_KING, position.squares[blackKingSquare], "Black king square");
        double[] expectedMaterialScore = ForsythEdwardsNotation.getMaterialScore(position.squares);
        assertEquals(expectedMaterialScore[1], position.materialScore[1], "White material score");
        assertEquals(expectedMaterialScore[2], position.materialScore[2], "Black material score");

        if (ChessPositionHasher.computeHash(position.squares, position.white, position.castleState, position.enPassantSquare) != position.zobristHash) {
            fail(ChessFunctions.getBoardStr(position));
        }
    }

    public static void assertPositionsEqual(ChessPosition expected, ChessPosition actual) {
        assertEquals(ChessFunctions.getBoardStr(expected), ChessFunctions.getBoardStr(actual));
        assertEquals(expected.currentPlayer, actual.currentPlayer, "Current player");
        assertEquals(expected.castleState, actual.castleState, "Castle state");
        assertEquals(expected.enPassantSquare, actual.enPassantSquare, "En passant square");
        assertEquals(expected.kingSquares[1], actual.kingSquares[1], "White king square");
        assertEquals(expected.kingSquares[2], actual.kingSquares[2], "Black king square");
        assertEquals(expected.materialScore[1], actual.materialScore[1], "White material score");
        assertEquals(expected.materialScore[2], actual.materialScore[2], "Black material score");
        assertEquals(expected.halfMoveClock, actual.halfMoveClock, "Half move clock");
        assertEquals(expected.positionHistory.plyCount, actual.positionHistory.plyCount, "Ply count");
        for (int i = 0; i < expected.positionHistory.plyCount; ++i) {
            UndoChessMove expectedUndoMove = expected.positionHistory.undoChessMoves[i];
            UndoChessMove actualUndoMove = actual.positionHistory.undoChessMoves[i];
            if (expectedUndoMove == null) {
                assertNull(actualUndoMove);
                continue;
            }
            assertEquals(expectedUndoMove.priorCastleState, actualUndoMove.priorCastleState, "Castle state " + i);
            assertEquals(expectedUndoMove.priorEnPassantSquare, actualUndoMove.priorEnPassantSquare, "En passant Square " + i);
            assertEquals(expectedUndoMove.priorHalfMoveClock, actualUndoMove.priorHalfMoveClock, "Half move clock " + i);
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
