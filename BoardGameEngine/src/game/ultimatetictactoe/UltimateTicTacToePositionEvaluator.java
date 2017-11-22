package game.ultimatetictactoe;

import analysis.AnalysisResult;
import analysis.IPositionEvaluator;
import game.Coordinate;
import game.TwoPlayers;
import game.tictactoe.TicTacToeUtilities;

public class UltimateTicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, UltimateTicTacToePosition> {
	private static final int WINS_PER_BOARD = 8;

	@Override
	public double evaluate(UltimateTicTacToePosition position, int player) {
		int lastPlayer = TwoPlayers.otherPlayer(position.currentPlayer);
		if (TicTacToeUtilities.winExists(position.wonBoards, lastPlayer)) {
			return player == lastPlayer ? AnalysisResult.WIN : AnalysisResult.LOSS;
		} else {
			int opponent = TwoPlayers.otherPlayer(player);
			if (!UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards | position.fullBoards, opponent)
					&& !UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards | position.fullBoards, player)) {
				return AnalysisResult.DRAW;
			}
			int totalPlayerPossibleWins = 0;
			int totalOpponentPossibleWins = 0;
			int playerPossibleWonBoards = position.wonBoards;
			int opponentPossibleWonBoards = position.wonBoards;
			int n = 0;
			while (n < UltimateTicTacToePosition.BOARD_WIDTH) {
				int twoN = n << 1;
				if (((position.fullBoards >> twoN) & TwoPlayers.BOTH_PLAYERS) != TwoPlayers.UNPLAYED) {
					playerPossibleWonBoards |= TicTacToeUtilities.PLAYER_POS[opponent][n];
					opponentPossibleWonBoards |= TicTacToeUtilities.PLAYER_POS[player][n];
				} else {
					int wonBoardInt = (position.wonBoards >> twoN) & TwoPlayers.BOTH_PLAYERS;
					if (wonBoardInt == TwoPlayers.UNPLAYED) {
						int countPossiblePlayerWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[n], opponent);
						int countPossibleOpponentWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[n], player);
						totalPlayerPossibleWins += countPossiblePlayerWins;
						totalOpponentPossibleWins += countPossibleOpponentWins;
						playerPossibleWonBoards |= (countPossiblePlayerWins > 0 ? player : opponent) << twoN;
						opponentPossibleWonBoards |= (countPossiblePlayerWins > 0 ? opponent : player) << twoN;
					} else if (wonBoardInt == player) {
						totalPlayerPossibleWins += WINS_PER_BOARD;
					} else {
						totalOpponentPossibleWins += WINS_PER_BOARD;
					}
				}
				++n;
			}

			int playerActualWins = UltimateTicTacToeUtilities.countPossibleWins(playerPossibleWonBoards, opponent);
			int opponentAcualWins = UltimateTicTacToeUtilities.countPossibleWins(opponentPossibleWonBoards, player);
			if (playerActualWins == 0 && opponentAcualWins == 0) {
				return AnalysisResult.DRAW;
			}
			return WINS_PER_BOARD * playerActualWins + totalPlayerPossibleWins
					- WINS_PER_BOARD * opponentAcualWins - totalOpponentPossibleWins;
		}
	}
}
