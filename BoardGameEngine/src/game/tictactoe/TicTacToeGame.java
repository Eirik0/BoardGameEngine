package game.tictactoe;

import analysis.ComputerPlayer;
import analysis.MinimaxStrategy;
import game.Coordinate;
import game.IGame;
import game.IPlayer;
import gui.GuiPlayer;

public class TicTacToeGame implements IGame<Coordinate, TicTacToePosition> {
	public static final String NAME = "Tic Tac Toe";

	private final IPlayer computerPlayer = new ComputerPlayer(new MinimaxStrategy<>(new TicTacToePositionEvaluator()), 2, "Computer", 500);

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
		return new IPlayer[] { GuiPlayer.HUMAN, computerPlayer };
	}

	@Override
	public IPlayer getDefaultPlayer() {
		return GuiPlayer.HUMAN;
	}

	@Override
	public TicTacToePosition newInitialPosition() {
		return new TicTacToePosition();
	}
}
