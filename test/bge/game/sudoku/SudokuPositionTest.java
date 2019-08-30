package bge.game.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.game.chess.ChessPositionPerfTest;
import bge.igame.IPosition;

public class SudokuPositionTest {
    @Test
    public void countAtDepthZero() {
        countPos(new SudokuPosition(), 0, 729);
    }

    @Test
    public void countAtDepthOne() {
        countPos(new SudokuPosition(), 1, 510300);
    }

    @Test
    @Disabled
    public void countAtDepthTwo() {
        countPos(new SudokuPosition(), 2, 342802044);
    }

    public static <M> void countPos(IPosition<M> position, int depth, long expectedPositions) {
        long startPos = System.currentTimeMillis();
        long countPositions = ChessPositionPerfTest.countPositions(position, depth);
        long posTime = System.currentTimeMillis() - startPos;
        long posPerSec = (long) (((double) countPositions / posTime) * 1000);
        System.out.println(position.getClass().getSimpleName() + "; D" + (depth + 1) + " " + countPositions + ", " + posTime + "ms, pps= " + posPerSec);
        assertEquals(expectedPositions, countPositions);
    }
}
