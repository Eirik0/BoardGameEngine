package bge.game;

public class PositionChangedInfo<M> {
    public final IPosition<M> position;
    public final IPlayer currentPlayer;
    public final MoveHistory<M> moveHistory;

    public PositionChangedInfo(IPosition<M> position, IPlayer currentPlayer, MoveHistory<M> moveHistory) {
        this.position = position;
        this.currentPlayer = currentPlayer;
        this.moveHistory = moveHistory;
    }
}
