package bge.game.photosynthesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import bge.igame.Coordinate;
import bge.igame.IDeepCopy;
import bge.igame.IPosition;
import bge.igame.MoveList;

public final class PhotosynthesisPosition implements IPosition<IPhotosynthesisMove>, PhotosynthesisConstants {
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

    private PhotosynthesisPosition(
            int numPlayers,
            int currentPlayer,
            int firstPlayer,
            MainBoard mainBoard,
            PlayerBoard[] playerBoards,
            int setupPlayerRoundsRemaining,
            int playerRoundsRemaining,
            int[] scoringTokensRemaining) {
        this.numPlayers = numPlayers;
        this.currentPlayer = currentPlayer;
        this.firstPlayer = firstPlayer;
        this.mainBoard = mainBoard;
        this.playerBoards = playerBoards;
        this.setupPlayerRoundsRemaining = setupPlayerRoundsRemaining;
        this.playerRoundsRemaining = playerRoundsRemaining;
        this.scoringTokensRemaining = scoringTokensRemaining;
    }

    /** Gets the final score for each player.
     * 12 = win
     * 6 = draw against 1 player
     * 4 = draw against 2 players
     * 3 = draw against 3 players
     * 0 = loss
     * @return Array of results indexed by player, totaling to 12.
     */
    public int[] getResult() {
        final int[] adjustedScore = new int[numPlayers];

        // The player with the most victory points wins. In case of tie, the
        // winner is the player with the most seeds and trees on the player board.
        for (final Coordinate coord : ALL_COORDS) {
            final Tile tile = mainBoard.grid[coord.x][coord.y];
            if (tile.player != -1) {
                adjustedScore[tile.player] += 1;
            }
        }

        for (int i = 0; i < numPlayers; i++) {
            adjustedScore[i] += playerBoards[i].victoryPoints << 16;
        }

        List<Integer> winners = null;
        int winningScore = -1;

        for (int i = 0; i < numPlayers; i++) {
            if (adjustedScore[i] > winningScore) {
                winners = new ArrayList<>();
                winningScore = adjustedScore[i];
            }

            if (adjustedScore[i] >= winningScore) {
                winners.add(Integer.valueOf(i));
            }
        }

        final int[] result = new int[numPlayers];
        final int prize = 12 / winners.size();
        for (final int player : winners) {
            result[player] = prize;
        }

        return result;
    }

    @Override
    public IPosition<IPhotosynthesisMove> createCopy() {
        return new PhotosynthesisPosition(
                numPlayers,
                currentPlayer,
                firstPlayer,
                mainBoard.createCopy(),
                Arrays.stream(playerBoards).map(b -> b.createCopy()).toArray(PlayerBoard[]::new),
                setupPlayerRoundsRemaining,
                playerRoundsRemaining,
                scoringTokensRemaining.clone());
    }

    @Override
    public void getPossibleMoves(MoveList<IPhotosynthesisMove> moveList) {
        if (playerRoundsRemaining == 0) {
            return;
        }

        // Setup actions
        if (setupPlayerRoundsRemaining > 0) {
            for (final Coordinate coordinate : ALL_TILES[0]) {
                if (mainBoard.grid[coordinate.x][coordinate.y].player == -1) {
                    moveList.addQuietMove(new Setup(coordinate), this);
                }
            }
            return;
        }

        // End turn
        moveList.addQuietMove(new EndTurn(), this);

        // Buy actions
        for (int level = 0; level < 4; level++) {
            final PlayerBoard playerBoard = playerBoards[currentPlayer];
            final int buyable = playerBoard.buy[level];
            if (buyable > 0 && playerBoard.lightPoints >= PRICES[level][buyable - 1]) {
                moveList.addQuietMove(new Buy(level), this);
            }
        }

        // Upgrade and seed actions
        if (playerBoards[currentPlayer].lightPoints == 0) {
            return; // Seeds require 1 light point
        }

        for (final Coordinate coord : ALL_COORDS) {
            final Tile tile = mainBoard.grid[coord.x][coord.y];

            if (tile.lastTouchedPlayerRoundsRemaining == playerRoundsRemaining || tile.player != currentPlayer) {
                continue;
            }

            final int cost = tile.level + 1;
            final int newLevel = cost % 4;

            if (playerBoards[currentPlayer].available[newLevel] > 0 && playerBoards[currentPlayer].lightPoints >= cost) {
                moveList.addQuietMove(new Upgrade(coord), this);
            }

            if (tile.level > 0 && playerBoards[currentPlayer].available[0] > 0) {
                for (int d = 1; d <= tile.level; d++) {
                    for (final Coordinate coordinate : PATHS_OF_LENGTH.get(coord)[d]) {
                        if (mainBoard.grid[coordinate.x][coordinate.y].player == -1) {
                            moveList.addQuietMove(new Seed(coord, coordinate), this);
                        }
                    }
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
    public int hashCode() {
        final int prime = 31;
        int result = prime + currentPlayer;
        result = prime * result + mainBoard.hashCode();
        result = prime * result + numPlayers;
        result = prime * result + Arrays.hashCode(playerBoards);
        result = prime * result + playerRoundsRemaining;
        result = prime * result + Arrays.hashCode(scoringTokensRemaining);
        return prime * result + setupPlayerRoundsRemaining;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final PhotosynthesisPosition other = (PhotosynthesisPosition) obj;
        return currentPlayer == other.currentPlayer &&
                mainBoard.equals(other.mainBoard) &&
                numPlayers == other.numPlayers &&
                Arrays.equals(playerBoards, other.playerBoards) &&
                playerRoundsRemaining == other.playerRoundsRemaining &&
                Arrays.equals(scoringTokensRemaining, other.scoringTokensRemaining) &&
                setupPlayerRoundsRemaining == other.setupPlayerRoundsRemaining;
    }

    public static final class Tile implements IDeepCopy<Tile> {
        /** -1 denotes empty */
        int player = -1;
        int level = -1;
        int lastTouchedPlayerRoundsRemaining = Integer.MAX_VALUE;
        final int tileReward;

        public Tile(int tileReward) {
            this.tileReward = tileReward;
        }

        private Tile(int player, int level, int lastTouchedPlayerRoundsRemaining, int tileReward) {
            this.player = player;
            this.level = level;
            this.lastTouchedPlayerRoundsRemaining = lastTouchedPlayerRoundsRemaining;
            this.tileReward = tileReward;
        }

        @Override
        public Tile createCopy() {
            return new Tile(player, level, lastTouchedPlayerRoundsRemaining, tileReward);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * (prime * (prime * (prime + lastTouchedPlayerRoundsRemaining) + level) + player) + tileReward;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Tile other = (Tile) obj;
            return lastTouchedPlayerRoundsRemaining == other.lastTouchedPlayerRoundsRemaining &&
                    level == other.level && player == other.player && tileReward == other.tileReward;
        }
    }

    public static final class Setup implements IPhotosynthesisMove {
        final Coordinate coordinate;

        public Setup(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];
            tile.level = 1;
            tile.player = position.currentPlayer;

            position.setupPlayerRoundsRemaining--;
            --position.playerBoards[position.currentPlayer].available[1];
            position.currentPlayer = (position.currentPlayer + 1) % position.numPlayers;

            if (position.setupPlayerRoundsRemaining == 0) {
                position.doPhotosynthesis();
            }
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            if (position.setupPlayerRoundsRemaining == 0) {
                for (final PlayerBoard playerBoard : position.playerBoards) {
                    playerBoard.lightPoints = 0;
                }
            }

            final Tile tile = position.mainBoard.grid[coordinate.x][coordinate.y];
            tile.player = -1;
            tile.level = -1;

            position.currentPlayer = (position.currentPlayer + position.numPlayers - 1) % position.numPlayers;
            ++position.playerBoards[position.currentPlayer].available[1];
            position.setupPlayerRoundsRemaining++;
        }

        @Override
        public int hashCode() {
            return coordinate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Setup other = (Setup) obj;
            return coordinate.equals(other.coordinate);
        }

        @Override
        public String toString() {
            return "S" + coordinate.toString();
        }
    }

    public static final class Upgrade implements IPhotosynthesisMove {
        public final Coordinate coordinate;

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
        public String toString() {
            return "U" + coordinate;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime + coordinate.hashCode();
            result = prime * result + previousLastTouchedPlayerRoundsRemaining;
            result = prime * result + (returnedToPlayerBoard ? 1231 : 1237);
            return prime * result + tileRewardColumn;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Upgrade other = (Upgrade) obj;
            return coordinate.equals(other.coordinate)
                    && previousLastTouchedPlayerRoundsRemaining == other.previousLastTouchedPlayerRoundsRemaining
                    && returnedToPlayerBoard == other.returnedToPlayerBoard
                    && tileRewardColumn == other.tileRewardColumn;
        }
    }

    public static final class Buy implements IPhotosynthesisMove {
        final int buyColumn;

        public Buy(int buyColumn) {
            this.buyColumn = buyColumn;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];
            playerBoard.available[buyColumn]++;
            int i = --playerBoard.buy[buyColumn];
            playerBoard.lightPoints -= PRICES[buyColumn][i];
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            final PlayerBoard playerBoard = position.playerBoards[position.currentPlayer];
            playerBoard.lightPoints += PRICES[buyColumn][playerBoard.buy[buyColumn]++];
            playerBoard.available[buyColumn]--;
        }

        @Override
        public int hashCode() {
            return buyColumn;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Buy other = (Buy) obj;
            return buyColumn == other.buyColumn;
        }

        @Override
        public String toString() {
            return "B" + buyColumn;
        }
    }

    public static final class Seed implements IPhotosynthesisMove {
        final Coordinate source, dest;
        int sourceLastTouchedPlayerRoundsRemaining;

        public Seed(Coordinate source, Coordinate dest) {
            this.source = source;
            this.dest = dest;
        }

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            final Tile sourceTile = position.mainBoard.grid[source.x][source.y];
            final Tile destTile = position.mainBoard.grid[dest.x][dest.y];

            sourceLastTouchedPlayerRoundsRemaining = sourceTile.lastTouchedPlayerRoundsRemaining;
            sourceTile.lastTouchedPlayerRoundsRemaining = position.playerRoundsRemaining;

            destTile.player = position.currentPlayer;
            destTile.level = 0;

            position.playerBoards[position.currentPlayer].available[0]--;
            --position.playerBoards[position.currentPlayer].lightPoints;
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            final Tile sourceTile = position.mainBoard.grid[source.x][source.y];
            final Tile destTile = position.mainBoard.grid[dest.x][dest.y];

            ++position.playerBoards[position.currentPlayer].lightPoints;
            position.playerBoards[position.currentPlayer].available[0]++;

            sourceTile.lastTouchedPlayerRoundsRemaining = sourceLastTouchedPlayerRoundsRemaining;

            destTile.player = -1;
            destTile.level = -1;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * (prime * (prime + source.hashCode()) + dest.hashCode()) + sourceLastTouchedPlayerRoundsRemaining;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Seed other = (Seed) obj;
            return source.equals(other.source) && dest.equals(other.dest)
                    && sourceLastTouchedPlayerRoundsRemaining == other.sourceLastTouchedPlayerRoundsRemaining;
        }

        @Override
        public String toString() {
            return "S" + source.toString() + "-" + dest.toString();
        }
    }

    public static final class EndTurn implements IPhotosynthesisMove {
        int[] previousPlayerLightPoints;

        @Override
        public void applyMove(PhotosynthesisPosition position) {
            position.currentPlayer = (position.currentPlayer + 1) % position.numPlayers;
            position.playerRoundsRemaining--;
            if (position.currentPlayer == position.firstPlayer) {
                position.firstPlayer = (position.firstPlayer + 1) % position.numPlayers;
                position.currentPlayer = position.firstPlayer;

                previousPlayerLightPoints = Arrays.stream(position.playerBoards).mapToInt(pb -> pb.lightPoints).toArray();
                position.doPhotosynthesis();
            }
        }

        @Override
        public void unapplyMove(PhotosynthesisPosition position) {
            if (position.currentPlayer == position.firstPlayer) {
                // Restore points before photosynthesis phase
                for (int player = 0; player < previousPlayerLightPoints.length; player++) {
                    position.playerBoards[player].lightPoints = previousPlayerLightPoints[player];
                }

                position.firstPlayer = (position.firstPlayer + position.numPlayers - 1) % position.numPlayers;
                position.currentPlayer = (position.firstPlayer + position.numPlayers - 1) % position.numPlayers;
            } else {
                position.currentPlayer = (position.currentPlayer + position.numPlayers - 1) % position.numPlayers;
            }

            position.playerRoundsRemaining++;
        }

        @Override
        public String toString() {
            return "End";
        }
    }

    /** Represents the main hexagonal board where players compete. */
    public static final class MainBoard implements IDeepCopy<MainBoard> {
        static final int AXIS_LENGTH = 7;

        final Tile[][] grid;

        public MainBoard() {
            grid = new Tile[AXIS_LENGTH][AXIS_LENGTH];

            for (int tileReward = 0; tileReward < ALL_TILES.length; tileReward++) {
                for (final Coordinate coord : ALL_TILES[tileReward]) {
                    grid[coord.x][coord.y] = new Tile(tileReward);
                }
            }
        }

        private MainBoard(Tile[][] grid) {
            this.grid = grid;
        }

        @Override
        public MainBoard createCopy() {
            final Tile[][] newGrid = new Tile[AXIS_LENGTH][AXIS_LENGTH];

            for (int i = 0; i < AXIS_LENGTH; i++) {
                for (int j = 0; j < AXIS_LENGTH; j++) {
                    newGrid[i][j] = grid[i][j] == null ? null : grid[i][j].createCopy();
                }
            }

            return new MainBoard(newGrid);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(grid);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final MainBoard other = (MainBoard) obj;
            return Arrays.deepEquals(grid, other.grid);
        }

        int[][] getShadowMap(int sunPosition) {
            final int[][] map = new int[AXIS_LENGTH][AXIS_LENGTH];
            // Loop over all the trees and compute their shadows
            for (final Coordinate[] coords : ALL_TILES) {
                for (final Coordinate coord : coords) {
                    final Tile tile = grid[coord.x][coord.y];
                    if (tile.level > 0) {
                        if (sunPosition == 0) {
                            for (int dx = 1; dx <= tile.level; dx++) {
                                updateMapHelper(grid, coord.x + dx, coord.y + dx, map, tile.level);
                            }
                        } else if (sunPosition == 1) {
                            for (int dy = 1; dy <= tile.level; dy++) {
                                updateMapHelper(grid, coord.x, coord.y + dy, map, tile.level);
                            }
                        } else if (sunPosition == 2) {
                            for (int dx = 1; dx <= tile.level; dx++) {
                                updateMapHelper(grid, coord.x - dx, coord.y, map, tile.level);
                            }
                        } else if (sunPosition == 3) {
                            for (int dx = 1; dx <= tile.level; dx++) {
                                updateMapHelper(grid, coord.x - dx, coord.y - dx, map, tile.level);
                            }
                        } else if (sunPosition == 4) {
                            for (int dy = 1; dy <= tile.level; dy++) {
                                updateMapHelper(grid, coord.x, coord.y - dy, map, tile.level);
                            }
                        } else if (sunPosition == 5) {
                            for (int dx = 1; dx <= tile.level; dx++) {
                                updateMapHelper(grid, coord.x + dx, coord.y, map, tile.level);
                            }
                        }
                    }
                }
            }
            return map;
        }

        // Helper function to do bounds checking while updating the shadow map
        static void updateMapHelper(Tile[][] grid, int x, int y, int[][] map, int tileLevel) {
            if (x >= 0 && y >= 0 && x < AXIS_LENGTH && y < AXIS_LENGTH && grid[x][y] != null) {
                map[x][y] = Math.max(map[x][y], tileLevel);
            }
        }

        @SuppressWarnings("unchecked")
        static Map<Coordinate, List<Coordinate>[]> preloadShortestPaths() {
            final int[][] paths = computeAllPairsShortestPaths();

            final Map<Coordinate, List<Coordinate>[]> result = new HashMap<>();

            final int n = paths.length;

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    final int dist = paths[i][j];

                    List<Coordinate>[] innerMap = result.get(ALL_COORDS[i]);

                    if (innerMap == null) {
                        innerMap = new List[dist + 1];
                        result.put(ALL_COORDS[i], innerMap);
                    } else if (innerMap.length <= dist) {
                        List<Coordinate>[] innerMapNew = new List[dist + 1];
                        System.arraycopy(innerMap, 0, innerMapNew, 0, innerMap.length);
                        innerMap = innerMapNew;
                        result.put(ALL_COORDS[i], innerMap);
                    }

                    List<Coordinate> coords = innerMap[dist];

                    if (coords == null) {
                        coords = new ArrayList<>();
                        innerMap[dist] = coords;
                    }

                    coords.add(ALL_COORDS[j]);
                }
            }

            return result;
        }

        /** Implements Floyd-Warshall algorithm for all-pairs shortest paths */
        static int[][] computeAllPairsShortestPaths() {
            final int n = ALL_COORDS.length;
            final int[][] result = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) {
                        result[i][j] = 0;
                    } else {
                        if (areNeighbors(ALL_COORDS[i], ALL_COORDS[j])) {
                            result[i][j] = 1;
                        } else {
                            // "Infinity". Using max int causes an overflow in the addition below.
                            result[i][j] = 999;
                        }
                    }
                }
            }

            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        final int candidate = result[i][k] + result[k][j];
                        if (candidate < result[i][j]) {
                            result[i][j] = candidate;
                        }
                    }
                }
            }

            return result;
        }

        /** Determines whether hex coordinates a, b are neighbors */
        static boolean areNeighbors(Coordinate a, Coordinate b) {
            final int ax = a.x;
            final int ay = a.y;

            final int bx = b.x;
            final int by = b.y;

            return Math.abs(ax - bx) == 1 && ay == by
                    || Math.abs(ay - by) == 1 && ax == bx
                    || ax - bx == -1 && ay - by == -1
                    || ax - bx == 1 && ay - by == 1;
        }
    }

    int getSunPosition() {
        return ((18 * numPlayers - playerRoundsRemaining) / numPlayers) % 6;
    }

    int[][] getShadowMap() {
        return mainBoard.getShadowMap(getSunPosition());
    }

    void doPhotosynthesis() {
        final int[][] shadowMap = getShadowMap();

        for (final Coordinate coord : ALL_COORDS) {
            final Tile tile = mainBoard.grid[coord.x][coord.y];
            if (tile.player != -1 && tile.level > shadowMap[coord.x][coord.y]) {
                final PlayerBoard playerBoard = playerBoards[tile.player];
                playerBoard.lightPoints = Math.min(playerBoard.lightPoints + tile.level, MAX_LIGHT_POINTS);
            }
        }
    }

    /** Represents each player's own board, where they can buy seeds and trees. */
    public static final class PlayerBoard implements IDeepCopy<PlayerBoard> {
        int lightPoints;

        /** Length 4, with 0 = seeds, 1 = small, 2 = med, 3 = large */
        final int[] buy;

        final int[] available;

        int victoryPoints;

        public PlayerBoard() {
            lightPoints = 0;
            buy = new int[] { 4, 4, 3, 2 };
            available = new int[] { 2, 4, 1, 0 };
        }

        private PlayerBoard(int lightPoints, int[] buy, int[] available, int victoryPoints) {
            this.lightPoints = lightPoints;
            this.buy = buy;
            this.available = available;
            this.victoryPoints = victoryPoints;
        }

        @Override
        public PlayerBoard createCopy() {
            int[] buyCopy = new int[buy.length];
            System.arraycopy(buy, 0, buyCopy, 0, buy.length);
            int[] availableCopy = new int[available.length];
            System.arraycopy(available, 0, availableCopy, 0, available.length);
            return new PlayerBoard(lightPoints, buyCopy, availableCopy, victoryPoints);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = prime + Arrays.hashCode(available);
            result = prime * result + Arrays.hashCode(buy);
            result = prime * result + lightPoints;
            return prime * result + victoryPoints;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final PlayerBoard other = (PlayerBoard) obj;
            return Arrays.equals(available, other.available) && Arrays.equals(buy, other.buy)
                    && lightPoints == other.lightPoints && victoryPoints == other.victoryPoints;
        }
    }
}
