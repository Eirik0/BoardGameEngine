package game;

import java.util.function.BiConsumer;

public class GameObserver {
	private BiConsumer<IPlayer, Integer> playerChangedConsumer;
	private Runnable gameEndedRunnable;

	public GameObserver() {
		playerChangedConsumer = (b, i) -> {
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

	public void setEndGameAction(Runnable gameEndedRunnable) {
		this.gameEndedRunnable = gameEndedRunnable;
	}

	public void notifyGameEnded() {
		gameEndedRunnable.run();
	}
}
