package game;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GameObserver<M, P extends IPosition<M, P>> {
	private BiConsumer<IPlayer, Integer> playerChangedConsumer;
	private Consumer<P> positionChangedConsumer;
	private Consumer<Integer> gamePausedConsumer;

	public GameObserver() {
		playerChangedConsumer = (b, i) -> {
		};
		positionChangedConsumer = p -> {
		};
		gamePausedConsumer = i -> {
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

	public void setGamePausedAction(Consumer<Integer> gamePausedConsumer) {
		this.gamePausedConsumer = gamePausedConsumer;
	}

	public void notifyGamePaused(int playerNum) {
		gamePausedConsumer.accept(playerNum);
	}
}
