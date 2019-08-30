package bge.game.photosynthesis;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.igame.MoveList;

public class PhotosynthesisPositionEvaluator implements IPositionEvaluator<IPhotosynthesisMove, PhotosynthesisPosition> {
    @Override
    public double evaluate(PhotosynthesisPosition position, MoveList<IPhotosynthesisMove> possibleMoves) {
        if (possibleMoves.size() == 0) {
            int[] result = position.getResult();
            int currentPlayer = position.getCurrentPlayer();
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
        return 0;
    }
}
