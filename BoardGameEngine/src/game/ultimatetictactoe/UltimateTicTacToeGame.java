package game.ultimatetictactoe;

import analysis.ComputerPlayer;
import analysis.MinimaxStrategy;
import game.IGame;
import game.IPlayer;
import gui.GuiPlayer;

public class UltimateTicTacToeGame implements IGame<UTTTCoordinate, UltimateTicTacToePosition> {
	public static final String NAME = "Ultimate Tic Tac Toe";

	private final ComputerPlayer computer = newComputerPlayer(2, 3000);

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return 2;
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
	public UltimateTicTacToePosition newInitialPosition() {
		return new UltimateTicTacToePosition();
	}

	public static ComputerPlayer newComputerPlayer(int numWorkers, long msToWait) {
		return new ComputerPlayer(new MinimaxStrategy<>(new UltimateTicTacToePositionEvaluator()), numWorkers, "Computer", msToWait);
	}
}
