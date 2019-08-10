package game.papersoccer;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.MoveList;
import game.TwoPlayers;

public class PaperSoccerPositionEvaluator implements IPositionEvaluator<Integer, PaperSoccerPosition> {
    // Scores are (player distance) - (other player distance)

    private static final int[] P1_SCORES = new int[] {
            0, 0, 0, 0, 0, 8, 7, 8, 0, 0, 0, 0, 0,
            0, 0, 4, 4, 5, 6, 6, 6, 5, 4, 4, 0, 0,
            0, 0, 4, 4, 5, 6, 6, 6, 5, 4, 4, 0, 0,
            0, 0, 3, 3, 4, 4, 5, 4, 4, 3, 3, 0, 0,
            0, 0, 2, 2, 2, 3, 3, 3, 2, 2, 2, 0, 0,
            0, 0, 1, 1, 1, 1, 2, 1, 1, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, -1, -1, -1, -1, -2, -1, -1, -1, -1, 0, 0,
            0, 0, -2, -2, -2, -3, -3, -3, -2, -2, -2, 0, 0,
            0, 0, -3, -3, -4, -4, -5, -4, -4, -3, -3, 0, 0,
            0, 0, -4, -4, -5, -6, -6, -6, -5, -4, -4, 0, 0,
            0, 0, -4, -4, -5, -6, -6, -6, -5, -4, -4, 0, 0,
            0, 0, 0, 0, 0, -8, -7, -8, 0, 0, 0, 0, 0
    };
    private static final int[] P2_SCORES = new int[] {
            0, 0, 0, 0, 0, -8, -7, -8, 0, 0, 0, 0, 0,
            0, 0, -4, -4, -5, -6, -6, -6, -5, -4, -4, 0, 0,
            0, 0, -4, -4, -5, -6, -6, -6, -5, -4, -4, 0, 0,
            0, 0, -3, -3, -4, -4, -5, -4, -4, -3, -3, 0, 0,
            0, 0, -2, -2, -2, -3, -3, -3, -2, -2, -2, 0, 0,
            0, 0, -1, -1, -1, -1, -2, -1, -1, -1, -1, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 1, 2, 1, 1, 1, 1, 0, 0,
            0, 0, 2, 2, 2, 3, 3, 3, 2, 2, 2, 0, 0,
            0, 0, 3, 3, 4, 4, 5, 4, 4, 3, 3, 0, 0,
            0, 0, 4, 4, 5, 6, 6, 6, 5, 4, 4, 0, 0,
            0, 0, 4, 4, 5, 6, 6, 6, 5, 4, 4, 0, 0,
    };

    @Override
    public double evaluate(PaperSoccerPosition position, MoveList<Integer> possibleMoves) {
        boolean isPlayerOne = position.currentPlayer == TwoPlayers.PLAYER_1;
        int ballLocation = position.ballLocation;
        if (position.gameOver) {
            if (ballLocation < 8) {
                return isPlayerOne ? AnalysisResult.WIN : AnalysisResult.LOSS;
            }
            return isPlayerOne ? AnalysisResult.LOSS : AnalysisResult.WIN;
        }
        if (position.board[ballLocation] == PaperSoccerUtilities.ALL_DIRS) {
            return AnalysisResult.LOSS;
        }
        return isPlayerOne ? P1_SCORES[ballLocation] : P2_SCORES[ballLocation];
    }
}
