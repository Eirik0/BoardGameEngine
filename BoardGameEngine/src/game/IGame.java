package game;

public interface IGame<M, P extends IPosition<M, P>> {
	public String getName();

	public int getNumberOfPlayers();

	public P newInitialPosition();
}
