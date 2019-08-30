package bge.igame;

import bge.igame.player.IPlayer;

public class PositionChangedInfo<M> {
    public final IPosition<M> position;
    public final IPlayer currentPlayer;

    public PositionChangedInfo(IPosition<M> position, IPlayer currentPlayer) {
        this.position = position;
        this.currentPlayer = currentPlayer;
    }
}
