package bge.game.gomoku;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import bge.igame.ArrayMoveList;
import bge.igame.MoveList;
import bge.perf.PerfTest;

public class GomokuPositionTest {
    private static MoveList<Integer> getPossibleList(GomokuPosition position) {
        MoveList<Integer> possibleMoves = new ArrayMoveList<>(GomokuGame.MAX_MOVES);
        position.getPossibleMoves(possibleMoves);
        return possibleMoves;
    }

    @Test
    public void testWinsHorizontal() {
        GomokuPosition position = new GomokuPosition();
        int numMoves = 361;
        for (int x = 18; x > 14; --x) {
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x, 0));
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x, 1));
        }
        position.makeMove(GomokuUtilities.getMove(14, 0));
        assertEquals(0, getPossibleList(position).size());
    }

    @Test
    public void testWinsVertical() {
        GomokuPosition position = new GomokuPosition();
        int numMoves = 361;
        for (int y = 18; y > 14; --y) {
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(0, y));
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(1, y));
        }
        position.makeMove(GomokuUtilities.getMove(0, 14));
        assertEquals(0, getPossibleList(position).size());
    }

    @Test
    public void testWinsDiagonalRight() {
        GomokuPosition position = new GomokuPosition();
        int numMoves = 361;
        for (int x = 0; x < 4; ++x) {
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x, x));
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x + 1, x));
        }
        position.makeMove(GomokuUtilities.getMove(4, 4));
        assertEquals(0, getPossibleList(position).size());
    }

    @Test
    public void testWinsDiagonalLeft() {
        GomokuPosition position = new GomokuPosition();
        int numMoves = 361;
        for (int x = 14; x < 18; ++x) {
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x, 18 - x));
            assertEquals(numMoves--, getPossibleList(position).size());
            position.makeMove(GomokuUtilities.getMove(x + 1, 18 - x));
        }
        position.makeMove(GomokuUtilities.getMove(18, 0));
        assertEquals(0, getPossibleList(position).size());
    }

    @Test
    public void testCountPositions() {
        PerfTest.countPos(new GomokuPosition(), 0, 361);
        PerfTest.countPos(new GomokuPosition(), 1, 129960);
        PerfTest.countPos(new GomokuPosition(), 2, 46655640);
    }
}
