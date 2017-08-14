package gui;

import java.util.HashMap;
import java.util.Map;

import game.IGame;
import game.IPosition;
import gui.gamestate.IGameRenderer;

public class GameRegistry {
	private static final Map<Class<? extends IGame<?, ?>>, Class<? extends IGameRenderer<?, ?>>> gameStateMap = new HashMap<>();

	public static <M, P extends IPosition<M, P>> void registerGame(Class<? extends IGame<M, P>> gameClass, Class<? extends IGameRenderer<M, P>> gameRenderer) {
		gameStateMap.put(gameClass, gameRenderer);
	}

	public static <M, P extends IPosition<M, P>> IGame<M, P> newGame(Class<? extends IGame<M, P>> gameClass) {
		try {
			return gameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> IGameRenderer<M, P> newGameRenderer(Class<? extends IGame<M, P>> gameClass) {
		Class<? extends IGameRenderer<?, ?>> gameRenderer = gameStateMap.get(gameClass);
		if (gameRenderer == null) {
			throw new IllegalStateException("No game renderer found for " + gameClass.getName());
		}
		try {
			return (IGameRenderer<M, P>) gameRenderer.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Could not instantiate " + gameRenderer.getClass().getName(), e);
		}
	}
}
