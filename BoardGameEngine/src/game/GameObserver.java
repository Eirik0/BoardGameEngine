package game;

import java.util.function.Consumer;

public class GameObserver<M> {
	private Consumer<PositionChangedInfo<M>> positionChangedConsumer;
	private Runnable gameStoppedRunnable;
	private Runnable gameRunningRunnable;

	public GameObserver() {
		positionChangedConsumer = p -> {
		};
		gameStoppedRunnable = () -> {
		};
		gameRunningRunnable = () -> {
		};
	}

	public void setPositionChangedAction(Consumer<PositionChangedInfo<M>> positionChangedConsumer) {
		this.positionChangedConsumer = positionChangedConsumer;
	}

	public void notifyPositionChanged(PositionChangedInfo<M> positionChangedInfo) {
		positionChangedConsumer.accept(positionChangedInfo);
	}

	public void setGameRunningAction(Runnable gameRunningRunnable) {
		this.gameRunningRunnable = gameRunningRunnable;
	}

	public void notifyGameRunning() {
		gameRunningRunnable.run();
	}

	public void setGameStoppedAction(Runnable gameStoppedRunnable) {
		this.gameStoppedRunnable = gameStoppedRunnable;
	}

	public void notifyGameStopped() {
		gameStoppedRunnable.run();
	}
}
