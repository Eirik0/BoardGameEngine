package bge.game.ultimatetictactoe;

import bge.analysis.AnalysisResult;
import bge.analysis.IPositionEvaluator;
import bge.game.tictactoe.TicTacToeUtilities;
import bge.igame.Coordinate;
import bge.igame.MoveList;
import bge.igame.player.TwoPlayers;

public class UltimateTicTacToePositionEvaluator implements IPositionEvaluator<Coordinate, UltimateTicTacToePosition> {
    private static final int WINS_PER_BOARD = 8;

    @Override
    public double evaluate(UltimateTicTacToePosition position, MoveList<Coordinate> possibleMoves) {
        int player = position.currentPlayer;
        int opponent = TwoPlayers.otherPlayer(player);
        if (UltimateTicTacToeUtilities.winExists(position.wonBoards, opponent)) {
            return AnalysisResult.LOSS;
        } else {
            if (!UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards, opponent)
                    && !UltimateTicTacToeUtilities.hasPossibleWins(position.wonBoards, player)) {
                return AnalysisResult.DRAW;
            }
            int totalPlayerPossibleWins = 0;
            int totalOpponentPossibleWins = 0;
            int playerPossibleWonBoards = position.wonBoards;
            int opponentPossibleWonBoards = position.wonBoards;
            int n = 0;
            while (n < UltimateTicTacToePosition.BOARD_WIDTH) {
                int twoN = n << 1;
                int wonBoardInt = (position.wonBoards >> twoN) & TwoPlayers.BOTH_PLAYERS;
                if (wonBoardInt == TwoPlayers.BOTH_PLAYERS) {
                    playerPossibleWonBoards |= TicTacToeUtilities.getPlayerAtPosition(opponent, n);
                    opponentPossibleWonBoards |= TicTacToeUtilities.getPlayerAtPosition(player, n);
                } else if (wonBoardInt == player) {
                    totalPlayerPossibleWins += WINS_PER_BOARD;
                } else if (wonBoardInt == opponent) {
                    totalOpponentPossibleWins += WINS_PER_BOARD;
                } else {
                    int countPossiblePlayerWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[n], opponent);
                    int countPossibleOpponentWins = UltimateTicTacToeUtilities.countPossibleWins(position.boards[n], player);
                    totalPlayerPossibleWins += countPossiblePlayerWins;
                    totalOpponentPossibleWins += countPossibleOpponentWins;
                    playerPossibleWonBoards |= (countPossiblePlayerWins > 0 ? player : opponent) << twoN;
                    opponentPossibleWonBoards |= (countPossiblePlayerWins > 0 ? opponent : player) << twoN;
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
