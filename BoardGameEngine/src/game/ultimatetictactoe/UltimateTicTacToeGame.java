package game.ultimatetictactoe;

import game.IGame;
import game.TwoPlayers;

public class UltimateTicTacToeGame implements IGame<UTTTCoordinate, UltimateTicTacToePosition> {
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
