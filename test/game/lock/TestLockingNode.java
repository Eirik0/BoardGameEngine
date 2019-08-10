package game.lock;

import java.util.Arrays;

public class TestLockingNode {
    private volatile boolean isLocked;

    private TestLockingNode parent = null;
    private TestLockingNode[] moves = {};

    public TestLockingNode(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }

    public synchronized void waitForLock() {
        while (isLocked) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public TestLockingNode[] getPossibleMoves() {
        return moves;
    }

    public TestLockingNode setMoves(TestLockingNode... branches) {
        for (TestLockingNode child : branches) {
            child.parent = this;
        }
        moves = branches;
        return this;
    }

    public TestLockingNode getParent() {
        return parent;
    }

    @Override
    public String toString() {
        boolean[] movesArr = new boolean[moves.length];
        for (int i = 0; i < movesArr.length; i++) {
            movesArr[i] = moves[i].isLocked;
        }
        return isLocked + " -> " + Arrays.toString(movesArr);
    }
}
