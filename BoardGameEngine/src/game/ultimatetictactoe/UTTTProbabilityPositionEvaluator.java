package game.ultimatetictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.Coordinate;
import game.MoveList;
import game.TwoPlayers;
import game.ultimatetictactoe.UTTTProbabilityUtilities.WinCount;

public class UTTTProbabilityPositionEvaluator implements IPositionEvaluator<Coordinate, UltimateTicTacToePosition> {
	@Override
	public double evaluate(UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
		int player = position.currentPlayer;
		int opponent = TwoPlayers.otherPlayer(player);
		if (UltimateTicTacToeUtilities.winExists(position.wonBoards, opponent)) {
			return AnalysisResult.LOSS;
		} else {
			if (!UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards | position.fullBoards, opponent)
					&& !UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards | position.fullBoards, player)) {
				return AnalysisResult.DRAW;
			}
			WinCount[] probabilities = { UTTTProbabilityUtilities.WIN_COUNTS[position.boards[0]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[1]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[2]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[3]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[4]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[5]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[6]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[7]],
					UTTTProbabilityUtilities.WIN_COUNTS[position.boards[8]]
			};
			double p1W1 = probabilities[0].getP1Probability() * probabilities[1].getP1Probability() * probabilities[2].getP1Probability();
			double p1W2 = probabilities[3].getP1Probability() * probabilities[4].getP1Probability() * probabilities[5].getP1Probability();
			double p1W3 = probabilities[6].getP1Probability() * probabilities[7].getP1Probability() * probabilities[8].getP1Probability();
			double p1W4 = probabilities[0].getP1Probability() * probabilities[3].getP1Probability() * probabilities[6].getP1Probability();
			double p1W5 = probabilities[1].getP1Probability() * probabilities[4].getP1Probability() * probabilities[7].getP1Probability();
			double p1W6 = probabilities[2].getP1Probability() * probabilities[5].getP1Probability() * probabilities[8].getP1Probability();
			double p1W7 = probabilities[0].getP1Probability() * probabilities[4].getP1Probability() * probabilities[8].getP1Probability();
			double p1W8 = probabilities[2].getP1Probability() * probabilities[4].getP1Probability() * probabilities[6].getP1Probability();

			double p2W1 = probabilities[0].getP2Probability() * probabilities[1].getP2Probability() * probabilities[2].getP2Probability();
			double p2W2 = probabilities[3].getP2Probability() * probabilities[4].getP2Probability() * probabilities[5].getP2Probability();
			double p2W3 = probabilities[6].getP2Probability() * probabilities[7].getP2Probability() * probabilities[8].getP2Probability();
			double p2W4 = probabilities[0].getP2Probability() * probabilities[3].getP2Probability() * probabilities[6].getP2Probability();
			double p2W5 = probabilities[1].getP2Probability() * probabilities[4].getP2Probability() * probabilities[7].getP2Probability();
			double p2W6 = probabilities[2].getP2Probability() * probabilities[5].getP2Probability() * probabilities[8].getP2Probability();
			double p2W7 = probabilities[0].getP2Probability() * probabilities[4].getP2Probability() * probabilities[8].getP2Probability();
			double p2W8 = probabilities[2].getP2Probability() * probabilities[4].getP2Probability() * probabilities[6].getP2Probability();
			if (player == TwoPlayers.PLAYER_1) {
				return (p1W1 + p1W2 + p1W3 + p1W4 + p1W5 + p1W6 + p1W7 + p1W8) - (p2W1 + p2W2 + p2W3 + p2W4 + p2W5 + p2W6 + p2W7 + p2W8);
			} else {
				return (p2W1 + p2W2 + p2W3 + p2W4 + p2W5 + p2W6 + p2W7 + p2W8) - (p1W1 + p1W2 + p1W3 + p1W4 + p1W5 + p1W6 + p1W7 + p1W8);
			}
		}
	}
}
