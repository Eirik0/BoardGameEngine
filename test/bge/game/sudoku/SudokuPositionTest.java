package bge.game.sudoku;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.perf.PerfTest;

public class SudokuPositionTest {
    @Test
    public void countAtDepthZero() {
        PerfTest.countPos(new SudokuPosition(), 0, 729);
    }

    @Test
    public void countAtDepthOne() {
        PerfTest.countPos(new SudokuPosition(), 1, 510300);
    }

    @Test
    @Disabled
    public void countAtDepthTwo() {
        PerfTest.countPos(new SudokuPosition(), 2, 342802044);
    }
}
