package game.forkjoinexample;

@FunctionalInterface
public interface ForkObserver<M> {
	public void notifyForked(M move);
}
