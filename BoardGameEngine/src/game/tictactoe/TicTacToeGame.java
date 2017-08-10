package game.tictactoe;

import game.Coordinate;
import game.IGame;
import game.IPlayer;
import gui.GuiPlayer;

public class TicTacToeGame implements IGame<Coordinate, TicTacToePosition> {
	@Override
	public String getName() {
		return "Tic Tac Toe";
	}

	@Override
	public int getNumberOfPlayers() {
		return 2;
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
	public TicTacToePosition newInitialPosition() {
		return new TicTacToePosition();
	}
}
