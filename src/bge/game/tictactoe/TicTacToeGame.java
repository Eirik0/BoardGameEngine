package bge.game.tictactoe;

import bge.game.Coordinate;
import bge.game.IGame;
import bge.game.TwoPlayers;

public class TicTacToeGame implements IGame<Coordinate, TicTacToePosition> {
    public static final String NAME = "Tic Tac Toe";
    public static final int MAX_MOVES = TicTacToePosition.BOARD_WIDTH * TicTacToePosition.BOARD_WIDTH;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getNumberOfPlayers() {
        return TwoPlayers.NUMBER_OF_PLAYERS;
    }

    @Override
    public int getMaxMoves() {
        return MAX_MOVES;
    }

    @Override
    public TicTacToePosition newInitialPosition() {
        return new TicTacToePosition();
    }
}
