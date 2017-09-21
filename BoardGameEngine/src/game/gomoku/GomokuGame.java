package game.gomoku;

import analysis.ComputerPlayer;
import analysis.MinimaxStrategy;
import game.Coordinate;
import game.IGame;
import game.IPlayer;
import game.TwoPlayers;
import gui.GuiPlayer;

public class GomokuGame implements IGame<Coordinate, GomokuPosition> {
	public static final String NAME = "Gomoku";

	private final ComputerPlayer computer = newComputerPlayer(4, 5000);

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
		return new IPlayer[] { GuiPlayer.HUMAN, computer };
	}

	@Override
	public IPlayer getDefaultPlayer() {
		return GuiPlayer.HUMAN;
	}

	@Override
	public GomokuPosition newInitialPosition() {
		return new GomokuPosition();
	}

	public static ComputerPlayer newComputerPlayer(int numWorkers, long msToWait) {
		return new ComputerPlayer(new MinimaxStrategy<>(new GomokuPositionEvaluator()), numWorkers, "Computer", msToWait);
	}
}
