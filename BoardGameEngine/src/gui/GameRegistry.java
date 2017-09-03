package gui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import game.IGame;
import game.IPosition;
import gui.gamestate.IGameRenderer;

public class GameRegistry {
	private static final Map<String, GameRegistryItem> gameMap = new LinkedHashMap<>();
	//	private static final Map<Class<? extends IGame<?, ?>>, Class<? extends IGameRenderer<?, ?>>> gameStateMap = new HashMap<>();

	public static <M, P extends IPosition<M, P>> void registerGame(String gameName, Class<? extends IGame<M, P>> gameClass, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
		gameMap.put(gameName, new GameRegistryItem(gameClass, gameRendererClass));
	}

	public static Set<String> getGameNames() {
		return gameMap.keySet();
	}

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> IGame<M, P> newGame(String gameName) {
		try {
			return (IGame<M, P>) gameMap.get(gameName).gameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> IGameRenderer<M, P> newGameRenderer(String gameName) {
		try {
			return (IGameRenderer<M, P>) gameMap.get(gameName).gameRendererClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static class GameRegistryItem {
		final Class<? extends IGame<?, ?>> gameClass;
		final Class<? extends IGameRenderer<?, ?>> gameRendererClass;

		public GameRegistryItem(Class<? extends IGame<?, ?>> gameClass, Class<? extends IGameRenderer<?, ?>> gameRendererClass) {
			this.gameClass = gameClass;
			this.gameRendererClass = gameRendererClass;
		}
	}
}
