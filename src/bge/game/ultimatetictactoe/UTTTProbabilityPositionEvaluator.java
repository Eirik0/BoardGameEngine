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
        int opponent = TwoPlayers.otherPlayer(player);
        if (UltimateTicTacToeUtilities.winExists(position.wonBoards, opponent)) {
            return AnalysisResult.LOSS;
        } else {
            double p1W1 = p1Probs[position.boards[0]] * p1Probs[position.boards[1]] * p1Probs[position.boards[2]];
            double p1W2 = p1Probs[position.boards[3]] * p1Probs[position.boards[4]] * p1Probs[position.boards[5]];
            double p1W3 = p1Probs[position.boards[6]] * p1Probs[position.boards[7]] * p1Probs[position.boards[8]];
            double p1W4 = p1Probs[position.boards[0]] * p1Probs[position.boards[3]] * p1Probs[position.boards[6]];
            double p1W5 = p1Probs[position.boards[1]] * p1Probs[position.boards[4]] * p1Probs[position.boards[7]];
            double p1W6 = p1Probs[position.boards[2]] * p1Probs[position.boards[5]] * p1Probs[position.boards[8]];
            double p1W7 = p1Probs[position.boards[0]] * p1Probs[position.boards[4]] * p1Probs[position.boards[8]];
            double p1W8 = p1Probs[position.boards[2]] * p1Probs[position.boards[4]] * p1Probs[position.boards[6]];

            double p2W1 = p2Probs[position.boards[0]] * p2Probs[position.boards[1]] * p2Probs[position.boards[2]];
            double p2W2 = p2Probs[position.boards[3]] * p2Probs[position.boards[4]] * p2Probs[position.boards[5]];
            double p2W3 = p2Probs[position.boards[6]] * p2Probs[position.boards[7]] * p2Probs[position.boards[8]];
            double p2W4 = p2Probs[position.boards[0]] * p2Probs[position.boards[3]] * p2Probs[position.boards[6]];
            double p2W5 = p2Probs[position.boards[1]] * p2Probs[position.boards[4]] * p2Probs[position.boards[7]];
            double p2W6 = p2Probs[position.boards[2]] * p2Probs[position.boards[5]] * p2Probs[position.boards[8]];
            double p2W7 = p2Probs[position.boards[0]] * p2Probs[position.boards[4]] * p2Probs[position.boards[8]];
            double p2W8 = p2Probs[position.boards[2]] * p2Probs[position.boards[4]] * p2Probs[position.boards[6]];

            double p1Prob = p1W1 + p1W2 + p1W3 + p1W4 + p1W5 + p1W6 + p1W7 + p1W8;
            double p2Prob = p2W1 + p2W2 + p2W3 + p2W4 + p2W5 + p2W6 + p2W7 + p2W8;

            if (p1Prob == 0 && p2Prob == 0) {
                return AnalysisResult.DRAW;
            }

            return player == TwoPlayers.PLAYER_1 ? p1Prob - p2Prob : p2Prob - p1Prob;
        }
    }
}
