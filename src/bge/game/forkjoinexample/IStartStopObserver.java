package bge.game.forkjoinexample;

public interface IStartStopObserver {
    public void notifyPlyStarted();

    public void notifyPlyComplete(boolean searchStopped);
}
