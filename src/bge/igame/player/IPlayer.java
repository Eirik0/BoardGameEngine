package bge.igame.player;

import bge.igame.IPosition;

public interface IPlayer {
    <M> M getMove(IPosition<M> position);

    void notifyTurnEnded();

    void notifyGameEnded();
}
