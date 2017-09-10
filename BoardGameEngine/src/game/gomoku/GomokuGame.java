package game.gomoku;

import game.Coordinate;
import game.IGame;
import game.IPlayer;
import game.TwoPlayers;
import gui.GuiPlayer;

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
	public IPlayer[] getAvailablePlayers() {
		return new IPlayer[] { GuiPlayer.HUMAN };
	}

	@Override
	public IPlayer getDefaultPlayer() {
		return GuiPlayer.HUMAN;
	}

	@Override
	public GomokuPosition newInitialPosition() {
		return new GomokuPosition();
	}
}
