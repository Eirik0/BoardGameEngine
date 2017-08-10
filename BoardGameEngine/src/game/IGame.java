package game;

public interface IGame<M, P extends IPosition<M, P>> {

	public String getName();

	public int getNumberOfPlayers();

	public IPlayer[] getAvailablePlayers();

	public IPlayer getDefaultPlayer();

	public P newInitialPosition();
}
