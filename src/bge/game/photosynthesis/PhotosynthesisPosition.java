package bge.game.photosynthesis;

import java.util.Arrays;
import java.util.stream.IntStream;

import bge.game.Coordinate;
import bge.game.IPosition;
import bge.game.MoveList;

public class PhotosynthesisPosition implements IPosition<IPhotosynthesisMove> {
    // next todo: get setup moves function, photosynthesis phase impl
    // Game constants
    private static final int[][] SCORING_TOKENS = new int[][] {
            new int[] { 14, 14, 13, 13, 13, 12, 12, 12, 12 },
            new int[] { 17, 16, 16, 14, 14, 13, 13 },
            new int[] { 19, 18, 18, 17, 17 },
            new int[] { 22, 21, 20 }
    };

    private static final int[][] PRICES = new int[][] {
            new int[] { 2, 2, 1, 1 },
            new int[] { 3, 3, 2, 2 },
            new int[] { 4, 3, 3 },
            new int[] { 5, 4 }
    };

    static final Coordinate[][] ALL_TILES = new Coordinate[][] {
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

    final int numPlayers;

    int currentPlayer;

    final MainBoard mainBoard;

    final PlayerBoard[] playerBoards;

    int setupPlayerRoundsRemaining;

    final int playerRoundsRemaining;

    final int scoringTokensRemaining[];

    public PhotosynthesisPosition(int numPlayers) {
        this.numPlayers = numPlayers;

        mainBoard = new MainBoard();
        playerBoards = IntStream.range(0, numPlayers)
                .mapToObj(n -> new PlayerBoard())
                .toArray(PlayerBoard[]::new);
        setupPlayerRoundsRemaining = 2 * numPlayers;
        playerRoundsRemaining = 18 * numPlayers;

        scoringTokensRemaining = Arrays.stream(SCORING_TOKENS).mapToInt(a -> a.length).toArray();
    }

    @Override
    public void getPossibleMoves(MoveList<IPhotosynthesisMove> moveList) {
        if (setupPlayerRoundsRemaining > 0) {
            for (final Coordinate coordinate : ALL_TILES[0]) {
                if (mainBoard.grid[coordinate.x][coordinate.y].player == -1) {
                    moveList.addQuietMove(new Upgrade(coordinate), this);
                }
            }
        }
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void makeMove(IPhotosynthesisMove move) {
        move.applyMove(this);
    }

    @Override
    public void unmakeMove(IPhotosynthesisMove move) {
        move.unapplyMove(this);
    }

    @Override
    public IPosition<IPhotosynthesisMove> createCopy() {
        return new PhotosynthesisPosition(numPlayers);
    }

    public static class Tile {
        /** -1 denotes empty */
        int player = -1;
        int level = -1;
        int lastTouchedPlayerRoundsRemaining = Integer.MAX_VALUE;
        final int tileReward;

        public Tile(int tileReward) {
            this.tileReward = tileReward;
        }
    }

    public static class Setup implements IPhotosynthesisMove {
        private final Coordinate coordinate;

        public Setup(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];
            tile.level = 1;
            tile.player = position.currentPlayer;

            position.currentPlayer = (position.currentPlayer + 1) % position.numPlayers;
            position.setupPlayerRoundsRemaining--;
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];
            tile.player = -1;
            tile.level = 0;

            position.currentPlayer = (position.currentPlayer + position.numPlayers - 1) % position.numPlayers;
            position.setupPlayerRoundsRemaining++;
        }
    }

    public static class Upgrade implements IPhotosynthesisMove {
        private final Coordinate coordinate;

        private int tileRewardColumn = -1;

        private int previousLastTouchedPlayerRoundsRemaining;

        private boolean returnedToPlayerBoard = false;

        public Upgrade(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];
            previousLastTouchedPlayerRoundsRemaining = tile.lastTouchedPlayerRoundsRemaining;

            tile.lastTouchedPlayerRoundsRemaining = position.playerRoundsRemaining;

            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];

            if (playerBoard.buy[tile.level] < PRICES[tile.level].length) {
                playerBoard.buy[tile.level]++;
                returnedToPlayerBoard = true;
            }

            playerBoard.lightPoints -= ++tile.level;

            if (tile.level == 4) {
                tile.player = -1;
                tile.level = -1;

                // Find out the victory points
                for (int tileReward = tile.tileReward; tileReward >= 0; tileReward--) {
                    if (position.scoringTokensRemaining[tileReward] > 0) {
                        tileRewardColumn = tileReward;
                        break;
                    }
                }

                if (tileRewardColumn > -1) {
                    playerBoard.victoryPoints += SCORING_TOKENS[tileRewardColumn][--position.scoringTokensRemaining[tileRewardColumn]];
                }
            }
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];

            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];

            tile.lastTouchedPlayerRoundsRemaining = previousLastTouchedPlayerRoundsRemaining;

            if (tile.level == -1) {
                tile.level = 3;
                tile.player = position.currentPlayer;

                // Undo victory points
                if (tileRewardColumn > -1) {
                    playerBoard.victoryPoints -= SCORING_TOKENS[tileRewardColumn][position.scoringTokensRemaining[tileRewardColumn]++];
                }
            } else {
                tile.level--;
            }

            playerBoard.lightPoints += tile.level + 1;

            if (returnedToPlayerBoard) {
                playerBoard.buy[tile.level]--;
            }
        }
    }

    public static class Buy implements IPhotosynthesisMove {
        private final int buyColumn;

        public Buy(int buyColumn) {
            this.buyColumn = buyColumn;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            // TODO Auto-generated method stub

        }

    }

    /** Represents the main hexagonal board where players compete. */
    public class MainBoard {
        private static final int AXIS_LENGTH = 7;

        final Tile[][] grid;

        public MainBoard() {
            grid = new Tile[AXIS_LENGTH][AXIS_LENGTH];

            for (int tileReward = 0; tileReward < ALL_TILES.length; tileReward++) {
                for (final Coordinate coord : ALL_TILES[tileReward]) {
                    grid[coord.x][coord.y] = new Tile(tileReward);
                }
            }
        }
    }

    /** Represents each player's own board, where they can buy seeds and trees. */
    public class PlayerBoard {
        int lightPoints;

        /** Length 4, with 0 = seeds, 1 = small, 2 = med, 3 = large */
        final int[] buy;

        final int[] available;

        int victoryPoints;

        public PlayerBoard() {
            lightPoints = 0;
            buy = new int[] { 4, 4, 3, 2 };
            available = new int[] { 2, 2, 1, 0 };
        }
    }
}
