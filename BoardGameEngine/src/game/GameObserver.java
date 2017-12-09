package game;

import java.util.function.Consumer;

public class GameObserver<M, P extends IPosition<M, P>> {
	private Consumer<PositionChangedInfo<M, P>> positionChangedConsumer;
	private Runnable gamePausedRunnable;
	private Runnable gameRunningRunnable;

	public GameObserver() {
		positionChangedConsumer = p -> {
		};
		gamePausedRunnable = () -> {
		};
		gameRunningRunnable = () -> {
		};
	}

	public void setPositionChangedAction(Consumer<PositionChangedInfo<M, P>> positionChangedConsumer) {
		this.positionChangedConsumer = positionChangedConsumer;
	}

	public void notifyPositionChanged(PositionChangedInfo<M, P> positionChangedInfo) {
		positionChangedConsumer.accept(positionChangedInfo);
	}

	public void setGameRunningAction(Runnable gameRunningRunnable) {
		this.gameRunningRunnable = gameRunningRunnable;
	}

	public void notifyGameRunning() {
		gameRunningRunnable.run();
	}

	public void setGamePausedAction(Runnable gamePausedRunnable) {
		this.gamePausedRunnable = gamePausedRunnable;
	}

	public void notifyGamePaused() {
		gamePausedRunnable.run();
	}
}
