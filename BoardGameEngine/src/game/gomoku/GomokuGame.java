package game.gomoku;

import game.Coordinate;
import game.IGame;
import game.TwoPlayers;

public class GomokuGame implements IGame<Coordinate, GomokuPosition> {
	public static final String NAME = "Gomoku";
	public static final int MAX_MOVES = GomokuPosition.BOARD_WIDTH * GomokuPosition.BOARD_WIDTH;

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
	public GomokuPosition newInitialPosition() {
		return new GomokuPosition();
	}
}
