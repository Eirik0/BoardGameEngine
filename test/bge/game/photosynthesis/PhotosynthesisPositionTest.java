package bge.game.photosynthesis;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import bge.game.Coordinate;
import bge.game.photosynthesis.PhotosynthesisPosition.Setup;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;

public class PhotosynthesisPositionTest {
    @Test
    public void TestFirstMove() {
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

        position.makeMove(new Setup(Coordinate.valueOf(0, 0)));
        assertEquals(1, position.currentPlayer);
    }

    @Test
    public void TestSetup() {
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

        position.makeMove(new Setup(Coordinate.valueOf(0, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(1, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(2, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(3, 0)));

        assertEquals(0, position.currentPlayer);
        assertEquals(0, position.setupPlayerRoundsRemaining);
        assertEquals(2 * 6 * 3, position.playerRoundsRemaining);

        for (int p = 0; p < 2; p++) {
            assertEquals(0, position.playerBoards[p].victoryPoints);
            assertEquals(0, position.playerBoards[p].lightPoints);
        }

        Arrays.stream(PhotosynthesisPosition.ALL_TILES).flatMap(x -> Arrays.stream(x)).forEach(coord -> {
            final Tile tile = position.mainBoard.grid[coord.x][coord.y];
            assertEquals(Integer.MAX_VALUE, tile.lastTouchedPlayerRoundsRemaining);
        });

        assertEquals(Integer.MAX_VALUE, position.mainBoard.grid[0][0].lastTouchedPlayerRoundsRemaining);
    }
}
