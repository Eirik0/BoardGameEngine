package bge.game.chess.fen;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bge.game.chess.ChessPosition;
import bge.game.chess.ChessPositionTest;

public class ForsythEdwardsNotationTest {
    @Test
    public void testPositionToString_Initial() {
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", ForsythEdwardsNotation.positionToString(new ChessPosition()));
    }

    @Test
    public void testPositionToString_OneMove() {
        ChessPosition position = new ChessPosition();
        ChessPositionTest.makeMove(position, "e2-e4");
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", ForsythEdwardsNotation.positionToString(position));
    }

    @Test
    public void testPositionToString_ThreeMove() {
        ChessPosition position = new ChessPosition();
        ChessPositionTest.makeMove(position, "e2-e4");
        ChessPositionTest.makeMove(position, "c7-c5");
        ChessPositionTest.makeMove(position, "g1-f3");
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", ForsythEdwardsNotation.positionToString(position));
    }

    @Test
    public void testStringToInitialPosition() {
        ChessPositionTest.assertPositionsEqual(new ChessPosition(),
                ForsythEdwardsNotation.stringToPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
    }
}
