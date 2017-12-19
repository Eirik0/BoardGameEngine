package game;

public interface IGame<M, P extends IPosition<M>> {
	public String getName();

	public int getNumberOfPlayers();

	public int getMaxMoves();

	public P newInitialPosition();
}
