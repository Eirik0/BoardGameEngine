package analysis.search;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class TreeSearchWorker {
    private final String name;

    private final Consumer<TreeSearchWorker> completedWorkerConsumer;

    private Thread thread;
    private volatile boolean notShutdown = true;
    private volatile boolean treeSearchSet = false;

    private final BlockingQueue<Runnable> runnableQueue = new ArrayBlockingQueue<>(1);

    public TreeSearchWorker(Consumer<TreeSearchWorker> completedWorkerConsumer) {
        this("Worker_" + ThreadNumber.getThreadNum("Worker"), completedWorkerConsumer);
    }

    public TreeSearchWorker(String name, Consumer<TreeSearchWorker> completedWorkerConsumer) {
        this.name = name;
        this.completedWorkerConsumer = completedWorkerConsumer;
    }

    private void maybeInitThread() {
        if (thread == null) {
            notShutdown = true;
            thread = new Thread(() -> {
                while (notShutdown) {
                    try {
                        runnableQueue.take().run();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, name + "_Thread_" + ThreadNumber.getThreadNum(TreeSearchWorker.class));

            thread.start();
        }
    }

    public void joinThread() {
        notShutdown = false;
        if (thread != null) {
            try {
                runnableQueue.put(() -> {
                    // We have to say do nothing if we are waiting to take from the queue
                });
                thread.join();
                thread = null;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void workOn(GameTreeSearch<?, ?> treeSearch) {
        treeSearchSet = false;
        maybeInitThread();
        try {
            runnableQueue.put(() -> {
                synchronized (this) {
                    treeSearchSet = true;
                    notify();
                }
                treeSearch.search();
                completedWorkerConsumer.accept(this);
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void waitForSearchToStart() {
        while (!treeSearchSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TreeSearchWorker other = (TreeSearchWorker) obj;
        return name.equals(other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
