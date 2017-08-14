package game.ultimatetictactoe;

import game.IGame;
import game.IPlayer;
import gui.GuiPlayer;

public class UltimateTicTacToeGame implements IGame<UTTTCoordinate, UltimateTicTacToePosition> {
	@Override
	public String getName() {
		return "Ultimate Tic Tac Toe";
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
	public UltimateTicTacToePosition newInitialPosition() {
		return new UltimateTicTacToePosition();
	}
}
