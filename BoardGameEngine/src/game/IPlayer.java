package game;

public interface IPlayer {
	public <M, P extends IPosition<M, P>> M getMove(IPosition<M, P> position);
}
