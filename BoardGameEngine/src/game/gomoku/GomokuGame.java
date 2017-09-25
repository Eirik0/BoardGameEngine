package game.gomoku;

import game.Coordinate;
import game.IGame;
import game.TwoPlayers;

public class GomokuGame implements IGame<Coordinate, GomokuPosition> {
	public static final String NAME = "Gomoku";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return TwoPlayers.NUMBER_OF_PLAYERS;
	}

	@Override
	public GomokuPosition newInitialPosition() {
		return new GomokuPosition();
	}
}
