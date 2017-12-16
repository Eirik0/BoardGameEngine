package analysis.search;

import game.IPosition;

@FunctionalInterface
public interface IGameTreeSearchJoin<M, P extends IPosition<M, P>> {
	public void accept(boolean searchCanceled, int currentPlayer, MoveWithResult<M> moveWithResult);
}
