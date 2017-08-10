package gui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import game.GameRunner;
import game.IGame;
import game.IPosition;

public class GameRegistry {
	private static final Map<Class<? extends IGame<?, ?>>, Class<? extends GameState>> gameStateMap = new HashMap<>();

	public static void registerGame(Class<? extends IGame<?, ?>> gameClass, Class<? extends GameState> gameState) {
		gameStateMap.put(gameClass, gameState);
	}

	public static <M, P extends IPosition<M, P>> IGame<M, P> newGame(Class<? extends IGame<M, P>> gameClass) {
		try {
			return gameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static GameState newGameState(Class<? extends IGame<?, ?>> gameClass, GameRunner<?, ?> gameRunner) {
		Class<? extends GameState> gameState = gameStateMap.get(gameClass);
		if (gameState == null) {
			throw new IllegalStateException("No game state found for " + gameClass.getName());
		}
		try {
			return gameState.getConstructor(gameRunner.getClass()).newInstance(gameRunner);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException("Could not instantiate " + gameState.getClass().getName() + " with " + gameRunner.getClass().getName(), e);
		}
	}
}
