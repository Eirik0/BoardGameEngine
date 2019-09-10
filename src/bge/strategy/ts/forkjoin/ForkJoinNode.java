package bge.strategy.ts.forkjoin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import bge.analysis.AnalysisResult;
import gt.util.Pair;

public class ForkJoinNode<M> {
    private final M move;
    private final IForkable<M> forkable;
    private final IJoin<M> join;

    private volatile AnalysisResult<M> result = null;

    private volatile boolean searchStarted = false;
    private volatile boolean searchCanceled = false;

    private final AtomicBoolean forked = new AtomicBoolean(false);
    private final AtomicBoolean joined = new AtomicBoolean(false);

    public ForkJoinNode(M move, IForkable<M> forkable, IJoin<M> joiner) {
        this.move = move;
        this.forkable = forkable;
        this.join = joiner;
    }

    public synchronized void search() {
        if (!isForkable()) {
            join(false, forkable.search());
        } else if (!forked.get()) {
            searchStarted = true;
            result = forkable.search();
            notify();
            if (!forked.get()) {
                join(searchCanceled, result);
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
        return move;
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

    public List<ForkJoinNode<M>> fork() {
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
            join(false, result);
            return Collections.emptyList();
        }

        return forkable.fork(join, move, result);
    }

    public void join(boolean searchCanceled, AnalysisResult<M> result) {
        if (joined.compareAndSet(false, true)) {
            join.join(searchCanceled, Pair.valueOf(move, result));
        }
    }
}
