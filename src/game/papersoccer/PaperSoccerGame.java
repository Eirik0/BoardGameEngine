package game.papersoccer;

import game.IGame;

public class PaperSoccerGame implements IGame<Integer, PaperSoccerPosition> {
	public static final String NAME = "Paper Soccer";
	public static final int MAX_MOVES = 8;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getNumberOfPlayers() {
		return 2;
	}

	@Override
	public int getMaxMoves() {
		return MAX_MOVES;
	}

	@Override
	public PaperSoccerPosition newInitialPosition() {
		return new PaperSoccerPosition();
	}
}
