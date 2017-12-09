package game;

public class PositionChangedInfo<M, P extends IPosition<M, P>> {
	public final P position;
	public final IPlayer currentPlayer;
	public final MoveHistory<M, P> moveHistory;

	public PositionChangedInfo(P position, IPlayer currentPlayer, MoveHistory<M, P> moveHistory) {
		this.position = position;
		this.currentPlayer = currentPlayer;
		this.moveHistory = moveHistory;
	}
}
