package bge.game;

public interface IPlayer {
    public <M, P extends IPosition<M>> M getMove(P position);

    public void notifyTurnEnded();

    public void notifyGameEnded();
}
