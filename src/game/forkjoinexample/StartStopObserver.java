package game.forkjoinexample;

public class StartStopObserver implements IStartStopObserver {
    @Override
    public void notifyPlyStarted() {
        ForkJoinExampleThreadTracker.searchStarted();
    }

    @Override
    public void notifyPlyComplete(boolean searchStopped) {
        ForkJoinExampleThreadTracker.searchComplete(searchStopped);
    }
}
