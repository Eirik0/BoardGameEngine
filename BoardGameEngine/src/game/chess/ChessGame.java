package game.chess;

import game.IGame;
import game.TwoPlayers;
import game.chess.move.IChessMove;

public class ChessGame implements IGame<IChessMove, ChessPosition> {
	public static final String NAME = "Chess";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return TwoPlayers.NUMBER_OF_PLAYERS;
	}

	@Override
	public ChessPosition newInitialPosition() {
		return new ChessPosition();
	}
}
