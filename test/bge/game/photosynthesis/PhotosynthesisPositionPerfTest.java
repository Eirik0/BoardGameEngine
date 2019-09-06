package bge.game.photosynthesis;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import bge.igame.ArrayMoveList;
import bge.perf.PerfTest;

public class PhotosynthesisPositionPerfTest {

    @Test
    public void testCountAtDepth0() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 0, 18);
    }

    @Test
    public void testCountAtDepth1() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 1, 18 * 17);
    }

    @Test
    public void testCountAtDepth2() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 2, 18 * 17 * 16);
    }

    @Test
    public void testCountAtDepth3() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 3, 18 * 17 * 16 * 15);
    }

    @Test
    public void testCountAtDepth4() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 4, 828568); // XXX is this correct ?
    }

    @Test
    public void testCountAtDepth5() {
        PerfTest.countPos(new PhotosynthesisPosition(2), 5, 6443828); // XXX is this correct ?
    }

    @Test
    @Disabled
    public void testCountAtDepth8After5Moves() {
        ArrayMoveList<IPhotosynthesisMove> moveList = new ArrayMoveList<>(64);
        PhotosynthesisPosition position = new PhotosynthesisPosition(2);
        for (int i = 0; i < 5; ++i) {
            position.getPossibleMoves(moveList);
            position.makeMove(moveList.get(0));
            moveList.clear();
        }
        PerfTest.countPos(position, 8, 10726762); // XXX is this correct ?
    }
}
