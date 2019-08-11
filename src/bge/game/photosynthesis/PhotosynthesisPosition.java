package bge.game.photosynthesis;

import java.util.Arrays;
import java.util.stream.IntStream;

import bge.game.Coordinate;
import bge.game.IPosition;
import bge.game.MoveList;

public class PhotosynthesisPosition implements IPosition<IPhotosynthesisMove> {
    // Game constants
    static final int[][] SCORING_TOKENS = new int[][] {
            new int[] { 12, 12, 12, 12, 13, 13, 13, 14, 14 },
            new int[] { 13, 13, 14, 14, 16, 16, 17 },
            new int[] { 17, 17, 18, 18, 19 },
            new int[] { 20, 20, 21 }
    };

    static final int[][] PRICES = new int[][] {
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

    int firstPlayer;

    final MainBoard mainBoard;

    final PlayerBoard[] playerBoards;

    int setupPlayerRoundsRemaining;

    int playerRoundsRemaining;

    final int scoringTokensRemaining[];

    public PhotosynthesisPosition(int numPlayers) {
        this.numPlayers = numPlayers;

        currentPlayer = 0;
        firstPlayer = 0;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + currentPlayer;
        result = prime * result + ((mainBoard == null) ? 0 : mainBoard.hashCode());
        result = prime * result + numPlayers;
        result = prime * result + Arrays.hashCode(playerBoards);
        result = prime * result + playerRoundsRemaining;
        result = prime * result + Arrays.hashCode(scoringTokensRemaining);
        result = prime * result + setupPlayerRoundsRemaining;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PhotosynthesisPosition other = (PhotosynthesisPosition) obj;
        if (currentPlayer != other.currentPlayer) {
            return false;
        }
        if (mainBoard == null) {
            if (other.mainBoard != null) {
                return false;
            }
        } else if (!mainBoard.equals(other.mainBoard)) {
            return false;
        }
        if (numPlayers != other.numPlayers) {
            return false;
        }
        if (!Arrays.equals(playerBoards, other.playerBoards)) {
            return false;
        }
        if (playerRoundsRemaining != other.playerRoundsRemaining) {
            return false;
        }
        if (!Arrays.equals(scoringTokensRemaining, other.scoringTokensRemaining)) {
            return false;
        }
        if (setupPlayerRoundsRemaining != other.setupPlayerRoundsRemaining) {
            return false;
        }
        return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + lastTouchedPlayerRoundsRemaining;
            result = prime * result + level;
            result = prime * result + player;
            result = prime * result + tileReward;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tile other = (Tile) obj;
            if (lastTouchedPlayerRoundsRemaining != other.lastTouchedPlayerRoundsRemaining) {
                return false;
            }
            if (level != other.level) {
                return false;
            }
            if (player != other.player) {
                return false;
            }
            if (tileReward != other.tileReward) {
                return false;
            }
            return true;
        }
    }

    public static class Setup implements IPhotosynthesisMove {
        final Coordinate coordinate;

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
            tile.level = -1;

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
            } else {
                playerBoard.available[tile.level]--;
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
                playerBoard.available[tile.level]++;
                tile.level--;
            }

            playerBoard.lightPoints += tile.level + 1;

            if (returnedToPlayerBoard) {
                playerBoard.buy[tile.level]--;
            }
        }
    }

    public static class Buy implements IPhotosynthesisMove {
        final int buyColumn;

        public Buy(int buyColumn) {
            this.buyColumn = buyColumn;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];
            playerBoard.buy[buyColumn]--;
            playerBoard.available[buyColumn]++;
            playerBoard.lightPoints -= PRICES[buyColumn][playerBoard.buy[buyColumn]];
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];
            playerBoard.lightPoints += PRICES[buyColumn][playerBoard.buy[buyColumn]];
            playerBoard.buy[buyColumn]++;
            playerBoard.available[buyColumn]--;
        }

    }

    public static class EndTurn implements IPhotosynthesisMove {
        private static EndTurn instance;

        private EndTurn() {
        }

        public static EndTurn getInstance() {
            if (instance == null) {
                instance = new EndTurn();
            }

            return instance;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            position.currentPlayer = (position.currentPlayer + 1) % position.numPlayers;
            position.playerRoundsRemaining--;
            if (position.currentPlayer == position.firstPlayer) {
                position.firstPlayer = (position.firstPlayer + 1) % position.numPlayers;
                position.currentPlayer = position.firstPlayer;
            }

        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            position.playerRoundsRemaining++;
            if (position.currentPlayer == position.firstPlayer) {
                position.firstPlayer = (position.firstPlayer + position.numPlayers - 1) % position.numPlayers;
                position.currentPlayer = (position.firstPlayer + position.numPlayers - 1) % position.numPlayers;
            } else {
                position.currentPlayer = (position.currentPlayer + position.numPlayers - 1) % position.numPlayers;
            }
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.deepHashCode(grid);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MainBoard other = (MainBoard) obj;
            if (!Arrays.deepEquals(grid, other.grid)) {
                return false;
            }
            return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(available);
            result = prime * result + Arrays.hashCode(buy);
            result = prime * result + lightPoints;
            result = prime * result + victoryPoints;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PlayerBoard other = (PlayerBoard) obj;
            if (!Arrays.equals(available, other.available)) {
                return false;
            }
            if (!Arrays.equals(buy, other.buy)) {
                return false;
            }
            if (lightPoints != other.lightPoints) {
                return false;
            }
            if (victoryPoints != other.victoryPoints) {
                return false;
            }
            return true;
        }
    }
}
