package bge.game.photosynthesis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import bge.game.photosynthesis.PhotosynthesisPosition.MainBoard;
import bge.igame.Coordinate;

public interface PhotosynthesisConstants {
    // Game constants
    int[][] SCORING_TOKENS = new int[][] {
            new int[] { 12, 12, 12, 12, 13, 13, 13, 14, 14 },
            new int[] { 13, 13, 14, 14, 16, 16, 17 },
            new int[] { 17, 17, 18, 18, 19 },
            new int[] { 20, 20, 21 }
    };

    int[][] PRICES = new int[][] {
            new int[] { 2, 2, 1, 1 },
            new int[] { 3, 3, 2, 2 },
            new int[] { 4, 3, 3 },
            new int[] { 5, 4 }
    };

    int MAX_LIGHT_POINTS = 20;

    Coordinate[][] ALL_TILES = new Coordinate[][] {
            new Coordinate[] {
                    Coordinate.valueOf(0, 0),
                    Coordinate.valueOf(1, 0),
                    Coordinate.valueOf(2, 0),
                    Coordinate.valueOf(3, 0),
                    Coordinate.valueOf(4, 1),
                    Coordinate.valueOf(5, 2),
                    Coordinate.valueOf(6, 3),
                    Coordinate.valueOf(6, 4),
                    Coordinate.valueOf(6, 5),
                    Coordinate.valueOf(6, 6),
                    Coordinate.valueOf(5, 6),
                    Coordinate.valueOf(4, 6),
                    Coordinate.valueOf(3, 6),
                    Coordinate.valueOf(2, 5),
                    Coordinate.valueOf(1, 4),
                    Coordinate.valueOf(0, 3),
                    Coordinate.valueOf(0, 2),
                    Coordinate.valueOf(0, 1)
            },
            new Coordinate[] {
                    Coordinate.valueOf(1, 1),
                    Coordinate.valueOf(2, 1),
                    Coordinate.valueOf(3, 1),
                    Coordinate.valueOf(4, 2),
                    Coordinate.valueOf(5, 3),
                    Coordinate.valueOf(5, 4),
                    Coordinate.valueOf(5, 5),
                    Coordinate.valueOf(4, 5),
                    Coordinate.valueOf(3, 5),
                    Coordinate.valueOf(2, 4),
                    Coordinate.valueOf(1, 3),
                    Coordinate.valueOf(1, 2)
            },
            new Coordinate[] {
                    Coordinate.valueOf(2, 2),
                    Coordinate.valueOf(3, 2),
                    Coordinate.valueOf(4, 3),
                    Coordinate.valueOf(4, 4),
                    Coordinate.valueOf(3, 4),
                    Coordinate.valueOf(2, 3),
            },
            new Coordinate[] {
                    Coordinate.valueOf(3, 3),
            }
    };

    Coordinate[] ALL_COORDS = Arrays.stream(ALL_TILES).flatMap(xs -> Arrays.stream(xs)).toArray(Coordinate[]::new);

    Map<Coordinate, List<Coordinate>[]> PATHS_OF_LENGTH = MainBoard.preloadShortestPaths();
}
