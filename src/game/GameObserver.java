package game;

import java.util.function.Consumer;

public class GameObserver<M> {
	private Consumer<PositionChangedInfo<M>> positionChangedConsumer;
	private Consumer<Boolean> gamePausedConsumer;
	private Runnable gameRunningRunnable;

	public GameObserver() {
		positionChangedConsumer = p -> {
		};
		gamePausedConsumer = b -> {
		};
		gameRunningRunnable = () -> {
		};
	}

	public void setPositionChangedAction(Consumer<PositionChangedInfo<M>> positionChangedConsumer) {
		this.positionChangedConsumer = positionChangedConsumer;
	}

	public void notifyPositionChanged(PositionChangedInfo<M> positionChangedInfo) {
		executeAction(() -> positionChangedConsumer.accept(positionChangedInfo));
	}

	public void setGameRunningAction(Runnable gameRunningRunnable) {
		this.gameRunningRunnable = gameRunningRunnable;
	}

	public void notifyGameRunning() {
		executeAction(gameRunningRunnable);
	}

	public void setGamePausedAction(Consumer<Boolean> gamePausedConsumer) {
		this.gamePausedConsumer = gamePausedConsumer;
	}

	public void notifyGamePaused(boolean gameEnded) {
		executeAction(() -> gamePausedConsumer.accept(Boolean.valueOf(gameEnded)));
	}

	private synchronized static void executeAction(Runnable runnable) {
		runnable.run();
	}
}
