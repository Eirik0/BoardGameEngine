package bge.game.ultimatetictactoe;

import org.junit.jupiter.api.Test;

import bge.igame.Coordinate;
import bge.perf.PerfTest;

public class UTTTPerfTest {
    @Test
    public void testCountAtDepth0() {
        // x 81
        PerfTest.countPos(new UltimateTicTacToePosition(), 0, 81);
    }

    @Test
    public void testCountAtDepth1() {
        // c  9, o 8
        // x 72, o 9
        PerfTest.countPos(new UltimateTicTacToePosition(), 1, 9 * 8 + 72 * 9);
    }

    @Test
    public void testCountAtDepth2() {
        PerfTest.countPos(new UltimateTicTacToePosition(), 2, 6336);
    }

    @Test
    public void testCountAtDepth3() {
        PerfTest.countPos(new UltimateTicTacToePosition(), 3, 55080);
    }

    @Test
    public void testCountAtDepth4() {
        PerfTest.countPos(new UltimateTicTacToePosition(), 4, 473256);
    }

    @Test
    public void testCountAtDepth5() {
        PerfTest.countPos(new UltimateTicTacToePosition(), 5, 4020960);
    }

    @Test
    public void testCountAtDepth6() {
        PerfTest.countPos(new UltimateTicTacToePosition(), 6, 33782544);
    }

    @Test
    public void testCountAtDepth7AfterOneMove() {
        System.out.print("One move: ");
        UltimateTicTacToePosition position = new UltimateTicTacToePosition();
        position.makeMove(Coordinate.valueOf(4, 4));
        PerfTest.countPos(position, 7, 26198592);
    }

    @Test
    public void testCountAtDepth7AfterTwoMoves() {
        System.out.print("Two moves: ");
        UltimateTicTacToePosition position = new UltimateTicTacToePosition();
        position.makeMove(Coordinate.valueOf(6, 4));
        position.makeMove(Coordinate.valueOf(4, 4));
        PerfTest.countPos(position, 7, 24279308);
    }
}
