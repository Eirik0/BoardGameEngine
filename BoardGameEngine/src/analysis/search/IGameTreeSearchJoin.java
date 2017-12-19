package analysis.search;

@FunctionalInterface
public interface IGameTreeSearchJoin<M> {
	public void accept(boolean searchCanceled, int currentPlayer, MoveWithResult<M> moveWithResult);
}
