package bge.game.ultimatetictactoe;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.ultimatetictactoe.UTTTProbabilityUtilities.WinCount;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

public class UTTTProbabilityPositionEvaluator implements IPositionEvaluator<Coordinate, UltimateTicTacToePosition> {
    private final double[] p1Probs;
    private final double[] p2Probs;

    public UTTTProbabilityPositionEvaluator() {
        WinCount[] winCounts = UTTTProbabilityUtilities.WIN_COUNTS;
        p1Probs = new double[winCounts.length];
        p2Probs = new double[winCounts.length];
        for (int i = 0; i < winCounts.length; ++i) {
            if (winCounts[i] == null) {
                continue;
            }
            p1Probs[i] = winCounts[i].p1Probability;
            p2Probs[i] = winCounts[i].p2Probability;
        }
    }

    @Override
    public double evaluate(UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
        int player = position.currentPlayer;
        if (UltimateTicTacToeUtilities.winExists(position.wonBoards, TwoPlayers.otherPlayer(player))) {
            return AnalysisResult.LOSS;
        } else if (possibleMoves.size() == 0) {
            return AnalysisResult.DRAW;
        }

        double p1B0 = p1Probs[position.boards[0]];
        double p1B1 = p1Probs[position.boards[1]];
        double p1B2 = p1Probs[position.boards[2]];
        double p1Prob = p1B0 * p1B1 * p1B2;
        double p1B3 = p1Probs[position.boards[3]];
        double p1B4 = p1Probs[position.boards[4]];
        double p1B5 = p1Probs[position.boards[5]];
        p1Prob += p1B3 * p1B4 * p1B5;
        double p1B6 = p1Probs[position.boards[6]];
        double p1B7 = p1Probs[position.boards[7]];
        double p1B8 = p1Probs[position.boards[8]];
        p1Prob += p1B6 * p1B7 * p1B8;
        p1Prob += p1B0 * p1B3 * p1B6;
        p1Prob += p1B1 * p1B4 * p1B7;
        p1Prob += p1B2 * p1B5 * p1B8;
        p1Prob += p1B0 * p1B4 * p1B8;
        p1Prob += p1B2 * p1B4 * p1B6;

        double p2B0 = p2Probs[position.boards[0]];
        double p2B1 = p2Probs[position.boards[1]];
        double p2B2 = p2Probs[position.boards[2]];
        double p2Prob = p2B0 * p2B1 * p2B2;
        double p2B3 = p2Probs[position.boards[3]];
        double p2B4 = p2Probs[position.boards[4]];
        double p2B5 = p2Probs[position.boards[5]];
        p2Prob += p2B3 * p2B4 * p2B5;
        double p2B6 = p2Probs[position.boards[6]];
        double p2B7 = p2Probs[position.boards[7]];
        double p2B8 = p2Probs[position.boards[8]];
        p2Prob += p2B6 * p2B7 * p2B8;
        p2Prob += p2B0 * p2B3 * p2B6;
        p2Prob += p2B1 * p2B4 * p2B7;
        p2Prob += p2B2 * p2B5 * p2B8;
        p2Prob += p2B0 * p2B4 * p2B8;
        p2Prob += p2B2 * p2B4 * p2B6;

        if (p1Prob == 0 && p2Prob == 0) {
            return AnalysisResult.DRAW;
        }

        return player == TwoPlayers.PLAYER_1 ? p1Prob - p2Prob : p2Prob - p1Prob;
    }
}
