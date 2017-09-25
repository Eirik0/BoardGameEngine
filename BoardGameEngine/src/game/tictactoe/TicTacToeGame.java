package game.tictactoe;

import game.Coordinate;
import game.IGame;
import game.TwoPlayers;

public class TicTacToeGame implements IGame<Coordinate, TicTacToePosition> {
	public static final String NAME = "Tic Tac Toe";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return TwoPlayers.NUMBER_OF_PLAYERS;
	}

	@Override
	public TicTacToePosition newInitialPosition() {
		return new TicTacToePosition();
	}
}
