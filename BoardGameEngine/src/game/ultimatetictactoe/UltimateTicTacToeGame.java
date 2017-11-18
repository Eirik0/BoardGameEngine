package game.ultimatetictactoe;

import game.Coordinate;
import game.IGame;
import game.TwoPlayers;

public class UltimateTicTacToeGame implements IGame<Coordinate, UltimateTicTacToePosition> {
	public static final String NAME = "Ultimate Tic Tac Toe";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return TwoPlayers.NUMBER_OF_PLAYERS;
	}

	@Override
	public UltimateTicTacToePosition newInitialPosition() {
		return new UltimateTicTacToePosition();
	}
}
