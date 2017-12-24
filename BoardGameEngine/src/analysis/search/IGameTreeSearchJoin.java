package analysis.search;

@FunctionalInterface
public interface IGameTreeSearchJoin<M> {
	public void accept(boolean searchCanceled, MoveWithResult<M> moveWithResult);
}
