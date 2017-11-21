package game;

public interface IPlayer {
	public <M, P extends IPosition<M, P>> M getMove(P position);

	public void notifyTurnEnded();

	public void notifyGameEnded();
}
