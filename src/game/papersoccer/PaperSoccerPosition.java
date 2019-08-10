package game.papersoccer;

import game.IPosition;
import game.MoveList;
import game.TwoPlayers;

public class PaperSoccerPosition implements IPosition<Integer> {
    final int[] board;
    int ballLocation;
    int currentPlayer;
    boolean gameOver;

    final PaperSoccerPositionHistory positionHistory;

    public PaperSoccerPosition() {
        this(PaperSoccerUtilities.newInitialPosition(), PaperSoccerUtilities.CENTER, TwoPlayers.PLAYER_1, new PaperSoccerPositionHistory(), false);
    }

    private PaperSoccerPosition(int[] board, int ballLocation, int currentPlayer, PaperSoccerPositionHistory positionHistory, boolean gameOver) {
        this.board = board;
        this.ballLocation = ballLocation;
        this.currentPlayer = currentPlayer;
        this.positionHistory = positionHistory;
        this.gameOver = gameOver;
    }

    @Override
    public void getPossibleMoves(MoveList<Integer> moveList) {
        if (gameOver) {
            return;
        }
        int[] directions = PaperSoccerUtilities.DIRECTIONS_REMAINING[board[ballLocation]];
        int i = 0;
        while (i < directions.length) {
            int newLocation = ballLocation + directions[i];
            moveList.addQuietMove(PaperSoccerUtilities.MOVES[newLocation], this);
            ++i;
        }
    }

    @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public void makeMove(Integer move) {
        int oldBallLocation = ballLocation;
        positionHistory.saveState(this);
        ballLocation = move.intValue();
        if (board[ballLocation] == 0) {
            currentPlayer = TwoPlayers.otherPlayer(currentPlayer);
        }
        PaperSoccerUtilities.updateBoard(board, oldBallLocation, ballLocation);
        if ((ballLocation > 4 && ballLocation < 8) || (ballLocation > 160 && ballLocation < 164)) {
            gameOver = true;
        }
    }

    @Override
    public void unmakeMove(Integer move) {
        positionHistory.unmakeMove(this);
        PaperSoccerUtilities.unupdateBoard(board, ballLocation, move.intValue());
        gameOver = false;
    }

    @Override
    public IPosition<Integer> createCopy() {
        int[] boardCopy = new int[PaperSoccerUtilities.BOARD_SIZE];
        System.arraycopy(board, 0, boardCopy, 0, PaperSoccerUtilities.BOARD_SIZE);
        return new PaperSoccerPosition(boardCopy, ballLocation, currentPlayer, positionHistory.createCopy(), gameOver);
    }
}
