package game.tictactoe;

import game.Coordinate;
import game.IGame;
import game.TwoPlayers;

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
