package bge.igame;

import java.util.function.Consumer;

import gt.util.BooleanConsumer;

public class GameObserver<M> {
    private Consumer<PositionChangedInfo<M>> positionChangedConsumer;
    private BooleanConsumer gamePausedConsumer;
    private Runnable gameRunningRunnable;

    public GameObserver() {
        positionChangedConsumer = p -> {
        };
        gamePausedConsumer = b -> {
        };
        gameRunningRunnable = () -> {
        };
    }

    public GameObserver<M> setPositionChangedAction(Consumer<PositionChangedInfo<M>> positionChangedConsumer) {
        this.positionChangedConsumer = positionChangedConsumer;
        return this;
    }

    public void notifyPositionChanged(PositionChangedInfo<M> positionChangedInfo) {
        positionChangedConsumer.accept(positionChangedInfo);
    }

    public GameObserver<M> setGameRunningAction(Runnable gameRunningRunnable) {
        this.gameRunningRunnable = gameRunningRunnable;
        return this;
    }

    public void notifyGameRunning() {
        gameRunningRunnable.run();
    }

    public GameObserver<M> setGamePausedAction(BooleanConsumer gamePausedConsumer) {
        this.gamePausedConsumer = gamePausedConsumer;
        return this;
    }

    public void notifyGamePaused(boolean gameEnded) {
        gamePausedConsumer.accept(gameEnded);
    }
}
