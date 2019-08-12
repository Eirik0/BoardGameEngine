package bge.game.photosynthesis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.Test;

import bge.game.Coordinate;
import bge.game.photosynthesis.PhotosynthesisPosition.Buy;
import bge.game.photosynthesis.PhotosynthesisPosition.EndTurn;
import bge.game.photosynthesis.PhotosynthesisPosition.PlayerBoard;
import bge.game.photosynthesis.PhotosynthesisPosition.Setup;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;
import bge.game.photosynthesis.PhotosynthesisPosition.Upgrade;

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

    @Test
    public void UnapplySetup() {
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

        final IPhotosynthesisMove[] moves = new IPhotosynthesisMove[] {
                new Setup(Coordinate.valueOf(0, 0)),
                new Setup(Coordinate.valueOf(1, 0)),
                new Setup(Coordinate.valueOf(2, 0)),
                new Setup(Coordinate.valueOf(3, 0))
        };

        for (final IPhotosynthesisMove move : moves) {
            position.makeMove(move);
        }

        for (int i = moves.length - 1; i >= 0; i--) {
            position.unmakeMove(moves[i]);
        }

        assertEquals(new PhotosynthesisPosition(2), position);
    }

    @Test
    public void ApplyAndUnapplyUpgrade() {
        // Helper to get starting positions with a "seeded" tile
        final Supplier<PhotosynthesisPosition> getStartingPosition = () -> {
            final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

            final Tile tile = position.mainBoard.grid[0][0];
            tile.level = 0;
            tile.player = 0;
            return position;
        };

        final PhotosynthesisPosition position = getStartingPosition.get();

        // Apply a series of upgrades until the tree is collected
        final IPhotosynthesisMove[] moves = new IPhotosynthesisMove[] {
                new Upgrade(Coordinate.valueOf(0, 0)),
                new Upgrade(Coordinate.valueOf(0, 0)),
                new Upgrade(Coordinate.valueOf(0, 0)),
                new Upgrade(Coordinate.valueOf(0, 0))
        };

        for (final IPhotosynthesisMove move : moves) {
            position.makeMove(move);
        }

        final Tile tile = position.mainBoard.grid[0][0];
        assertEquals(position.playerRoundsRemaining, tile.lastTouchedPlayerRoundsRemaining);
        assertEquals(-1, tile.level);
        assertEquals(-1, tile.player);
        assertEquals(14, position.playerBoards[0].victoryPoints);

        // Unmake all the moves and verify that the position is the same as we started with
        for (int i = moves.length - 1; i >= 0; i--) {
            position.unmakeMove(moves[i]);
        }

        final PhotosynthesisPosition expected = new PhotosynthesisPosition(2);
        assertEquals(getStartingPosition.get(), position);
    }

    /** Tests for effects of Upgrade moves on the player board state */
    @Test
    public void Upgrade_PlayerBoard() {
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);
        final PlayerBoard playerBoard = position.playerBoards[0];

        final Tile tile = position.mainBoard.grid[0][0];
        tile.level = 0;
        tile.player = 0;
        playerBoard.buy[0] = 3;

        position.makeMove(new Upgrade(Coordinate.valueOf(0, 0)));

        assertEquals(1, playerBoard.available[1]);
        assertEquals(4, playerBoard.buy[0]);

        position.makeMove(new Upgrade(Coordinate.valueOf(0, 0)));
        assertEquals(0, playerBoard.available[2]);
    }

    @Test
    public void ApplyUnapplyBuy() {
        final int priceOfEverything = Arrays.stream(PhotosynthesisPosition.PRICES)
                .flatMapToInt(x -> Arrays.stream(x))
                .sum();

        final Supplier<PhotosynthesisPosition> getPosition = () -> {
            final PhotosynthesisPosition position = new PhotosynthesisPosition(2);
            final PlayerBoard playerBoard = position.playerBoards[0];

            playerBoard.lightPoints = priceOfEverything;
            return position;
        };

        final PhotosynthesisPosition position = getPosition.get();

        final PlayerBoard playerBoard = position.playerBoards[0];
        final List<IPhotosynthesisMove> moves = new ArrayList<>();

        for (int i = 0; i < PhotosynthesisPosition.PRICES.length; i++) {
            for (int j = 0; j < PhotosynthesisPosition.PRICES[i].length; j++) {
                final Buy move = new Buy(i);
                moves.add(move);
                position.makeMove(move);
            }
        }

        assertEquals(0, playerBoard.lightPoints);

        Collections.reverse(moves);

        for (final IPhotosynthesisMove move : moves) {
            position.unmakeMove(move);
        }

        assertEquals(getPosition.get(), position);
    }

    @Test
    public void ApplyUnapplyEndTurn() {
        int expectedPlayerRounds = 2 * 6 * 3;
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

        position.makeMove(EndTurn.getInstance());
        expectedPlayerRounds--;

        assertEquals(1, position.currentPlayer);
        assertEquals(expectedPlayerRounds, position.playerRoundsRemaining);

        position.makeMove(EndTurn.getInstance());
        expectedPlayerRounds--;

        assertEquals(expectedPlayerRounds, position.playerRoundsRemaining);
        assertEquals(1, position.currentPlayer);

        position.unmakeMove(EndTurn.getInstance());
        position.unmakeMove(EndTurn.getInstance());

        assertEquals(new PhotosynthesisPosition(2), position);
    }

    @Test
    public void TestShadowMap() {
        // In this test we test a bunch of combinations of tree height + sun position to
        // see what shadows they create on the board. So bundle them into a class.
        class TestCase {
            private final int level;

            private final int sunPosition;

            private final Coordinate[] expectedShadowCoords;

            public TestCase(int level, int sunPosition, Coordinate[] expectedShadowCoords) {
                this.level = level;
                this.sunPosition = sunPosition;
                this.expectedShadowCoords = expectedShadowCoords;
            }

            public void validate() {
                final PhotosynthesisPosition position = new PhotosynthesisPosition(2);

                position.playerRoundsRemaining -= sunPosition;

                final Tile tile = position.mainBoard.grid[3][3];
                tile.level = level;
                tile.player = 0;

                final int[][] actualShadows = position.mainBoard.getShadowMap();

                final int[][] expectedShadows = new int[7][7];
                for (final Coordinate coord : expectedShadowCoords) {
                    expectedShadows[coord.x][coord.y] = level;
                }

                assertTrue(Arrays.deepEquals(expectedShadows, actualShadows));
            }
        }

        final TestCase[] testCases = new TestCase[] {
                // Sun at starting position, small tree, validate shadow at (4,3)
                new TestCase(1, 0, new Coordinate[] { Coordinate.valueOf(4, 3) }),
                new TestCase(2, 0, new Coordinate[] { Coordinate.valueOf(4, 3), Coordinate.valueOf(5, 3) }),
                // Height 3, validate shadows at (4,3), (5,3), (6,3)
                new TestCase(
                        3,
                        0,
                        new Coordinate[] {
                                Coordinate.valueOf(4, 3),
                                Coordinate.valueOf(5, 3),
                                Coordinate.valueOf(6, 3) }),

                // Test other sun positions
                new TestCase(1, 1, new Coordinate[] { Coordinate.valueOf(3, 4) }),
                new TestCase(1, 2, new Coordinate[] { Coordinate.valueOf(2, 4) }),
                new TestCase(1, 3, new Coordinate[] { Coordinate.valueOf(2, 3) }),
                new TestCase(1, 4, new Coordinate[] { Coordinate.valueOf(3, 2) }),
                new TestCase(1, 5, new Coordinate[] { Coordinate.valueOf(4, 2) })
        };

        for (final TestCase testCase : testCases) {
            testCase.validate();
        }
    }

    @Test
    public void TestEquals() {
        assertEquals(new PhotosynthesisPosition(2), new PhotosynthesisPosition(2));
        assertNotEquals(new PhotosynthesisPosition(3), new PhotosynthesisPosition(4));
    }

    @Test
    public void TestDeepCopy() {
        final PhotosynthesisPosition position = new PhotosynthesisPosition(2);
        final PhotosynthesisPosition copy = (PhotosynthesisPosition) position.createCopy();

        position.makeMove(new Setup(Coordinate.valueOf(0, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(1, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(2, 0)));
        position.makeMove(new Setup(Coordinate.valueOf(3, 0)));

        assertEquals(new PhotosynthesisPosition(2), copy);
    }
}
