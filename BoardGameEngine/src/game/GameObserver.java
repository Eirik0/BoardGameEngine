package game;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameObserver<M, P extends IPosition<M, P>> {
	private BiConsumer<IPlayer, Integer> playerChangedConsumer;
	private Consumer<P> positionChangedConsumer;
	private Runnable gameEndedRunnable;

	public GameObserver() {
		playerChangedConsumer = (b, i) -> {
		};
		positionChangedConsumer = p -> {
		};
		gameEndedRunnable = () -> {
		};
	}

	public void setPlayerChangedAction(BiConsumer<IPlayer, Integer> playerChangedConsumer) {
		this.playerChangedConsumer = playerChangedConsumer;
	}

	public void notifyPlayerChanged(IPlayer newPlayer, int playerNum) {
		playerChangedConsumer.accept(newPlayer, playerNum);
	}

	public void setPositionChangedAction(Consumer<P> positionChangedConsumer) {
		this.positionChangedConsumer = positionChangedConsumer;
	}

	public void notifyPositionChanged(P position) {
		positionChangedConsumer.accept(position);
	}

	public void setEndGameAction(Runnable gameEndedRunnable) {
		this.gameEndedRunnable = gameEndedRunnable;
	}

	public void notifyGameEnded() {
		gameEndedRunnable.run();
	}
}
