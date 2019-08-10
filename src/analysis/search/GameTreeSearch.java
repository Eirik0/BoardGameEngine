package analysis.search;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import analysis.AnalysisResult;
import analysis.strategy.IForkable;
import game.IPosition;

public class GameTreeSearch<M, P extends IPosition<M>> {
	private final IForkable<M, P> forkable;

	private final IGameTreeSearchJoin<M> treeSearchJoin;

	private volatile AnalysisResult<M> result = null;

	private volatile boolean searchStarted = false;
	private volatile boolean searchCanceled = false;

	private final AtomicBoolean forked = new AtomicBoolean(false);
	private final AtomicBoolean joined = new AtomicBoolean(false);

	public GameTreeSearch(IForkable<M, P> forkable, IGameTreeSearchJoin<M> treeSearchJoin) {
		this.forkable = forkable;
		this.treeSearchJoin = treeSearchJoin;
	}

	public synchronized void search() {
		if (!isForkable()) {
			result = forkable.search(treeSearchJoin);
			treeSearchJoin.accept(false, new MoveWithResult<>(forkable.getParentMove(), result));
		} else if (!forked.get()) {
			searchStarted = true;
			result = forkable.search(treeSearchJoin);
			notify();
			if (!forked.get()) {
				joined.set(true);
				treeSearchJoin.accept(searchCanceled, new MoveWithResult<>(forkable.getParentMove(), result));
			}
		}
	}

	public AnalysisResult<M> getResult() {
		return result;
	}

	public void stopSearch() {
		searchCanceled = true;
		forkable.stopSearch();
	}

	public M getParentMove() {
		return forkable.getParentMove();
	}

	public int getPlies() {
		return forkable.getPlies();
	}

	public int getRemainingBranches() {
		return forkable.getRemainingBranches();
	}

	public boolean isForkable() {
		return forkable.isForkable();
	}

	public List<GameTreeSearch<M, P>> fork() {
		forked.set(true);
		if (joined.get()) {
			return Collections.emptyList();
		}

		stopSearch();

		if (searchStarted) {
			synchronized (this) {
				while (result == null) {
					try {
						wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		if (forkable.getRemainingBranches() == 0) {
			synchronized (this) {
				if (!joined.getAndSet(true)) {
					treeSearchJoin.accept(false, new MoveWithResult<>(forkable.getParentMove(), result));
				}
				return Collections.emptyList();
			}
		}

		return forkable.fork(treeSearchJoin, result);
	}
}
