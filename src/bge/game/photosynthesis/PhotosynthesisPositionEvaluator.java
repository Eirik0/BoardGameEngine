package bge.game.photosynthesis;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.photosynthesis.PhotosynthesisPosition.Tile;
import bge.igame.Coordinate;
import bge.igame.MoveList;

public class PhotosynthesisPositionEvaluator implements IPositionEvaluator<IPhotosynthesisMove, PhotosynthesisPosition> {
    private static final double VICTORY_POINT_VALUE = (2 + 3 + 4 + 5 + 1 + 2 + 3 + 4) * 1.5;

    @Override
    public double evaluate(PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        int currentPlayer = position.getCurrentPlayer();
        if (possibleMoves.size() == 0) {
            int[] result = position.getResult();
            int currentPlayerScore = result[currentPlayer];
            boolean draw = true;
            for (int i = 0; i < result.length; ++i) {
                if (i != currentPlayer) {
                    int otherPlayerScore = result[i];
                    draw = draw && otherPlayerScore == currentPlayerScore;
                    if (otherPlayerScore > currentPlayer) {
                        return AnalysisResult.LOSS;
                    }
                }
            }
            if (draw) {
                return AnalysisResult.DRAW;
            }
            return AnalysisResult.WIN;
        }

        int[] lightPotential = new int[position.numPlayers];
        for (int i = 0; i < position.numPlayers; ++i) {
            lightPotential[i] = position.playerBoards[i].lightPoints;
        }
        int revolutionsRemaining = position.playerRoundsRemaining / position.numPlayers;
        int sunPosition = position.getSunPosition();
        for (int i = 0; i < revolutionsRemaining; ++i) {
            int[] lightForRound = new int[position.numPlayers];
            int[][] shadowMap = position.mainBoard.getShadowMap(sunPosition);
            for (final Coordinate coord : PhotosynthesisPosition.ALL_COORDS) {
                final Tile tile = position.mainBoard.grid[coord.x][coord.y];
                if (tile.player != -1 && tile.level > shadowMap[coord.x][coord.y]) {
                    lightForRound[tile.player] = Math.max(lightForRound[tile.player] + tile.level, PhotosynthesisPosition.MAX_LIGHT_POINTS);
                }
            }
            for (int j = 0; j < position.numPlayers; ++j) {
                lightPotential[j] += lightForRound[j];
            }
            --revolutionsRemaining;
            sunPosition = (sunPosition + 1) % 6;
        }

        int currentVictoryScore = position.playerBoards[currentPlayer].victoryPoints;
        int currentLightScore = lightPotential[currentPlayer];
        int maxOtherVictoryScore = 0;
        int maxOtherLightScore = 0;
        for (int i = 0; i < position.numPlayers; ++i) {
            if (i != currentPlayer) {
                maxOtherVictoryScore = Math.max(maxOtherVictoryScore, position.playerBoards[i].victoryPoints);
                maxOtherLightScore = Math.max(maxOtherLightScore, lightPotential[i]);
            }
        }
        return currentVictoryScore - maxOtherVictoryScore + (currentLightScore - maxOtherVictoryScore) / VICTORY_POINT_VALUE;
    }
}
