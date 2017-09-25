package gui;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import analysis.ComputerPlayer;
import analysis.IPositionEvaluator;
import analysis.strategy.AlphaBetaStrategy;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.MinimaxStrategy;
import game.IGame;
import game.IPlayer;
import game.IPosition;
import gui.gamestate.IGameRenderer;

public class GameRegistry {
	private static final Map<String, GameRegistryItem> gameMap = new LinkedHashMap<>();

	public static <M, P extends IPosition<M, P>> GameRegistryItem registerGame(String gameName, Class<? extends IGame<M, P>> gameClass, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
		GameRegistryItem gameRegistryItem = new GameRegistryItem(gameClass, gameRendererClass);
		gameMap.put(gameName, gameRegistryItem);
		return gameRegistryItem;
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

	public static Set<String> getPlayerNames(String gameName) {
		return gameMap.get(gameName).playerMap.keySet();
	}

	public static IPlayer getPlayer(String gameName, String playerName) {
		return gameMap.get(gameName).playerMap.get(playerName).get();
	}

	public static class GameRegistryItem {
		final Class<? extends IGame<?, ?>> gameClass;
		final Class<? extends IGameRenderer<?, ?>> gameRendererClass;
		final Map<String, Supplier<IPlayer>> playerMap = new LinkedHashMap<>(); // The first player is the default player

		public GameRegistryItem(Class<? extends IGame<?, ?>> gameClass, Class<? extends IGameRenderer<?, ?>> gameRendererClass) {
			this.gameClass = gameClass;
			this.gameRendererClass = gameRendererClass;
		}

		public GameRegistryItem registerPlayer(String playerName, IPlayer player) {
			playerMap.put(playerName, () -> player);
			return this;
		}

		public <M, P extends IPosition<M, P>> GameRegistryItem registerStrategy(String playerName, IDepthBasedStrategy<M, P> strategy, int numWorkers, long msPerMove) {
			playerMap.put(playerName, () -> new ComputerPlayer(strategy, numWorkers, playerName, msPerMove));
			return this;
		}

		public <M, P extends IPosition<M, P>> GameRegistryItem registerPositionEvaluator(String playerName, IPositionEvaluator<M, P> positionEvaluator, int numWorkers, long msPerMove) {
			playerMap.put(playerName + "_MM", () -> new ComputerPlayer(new MinimaxStrategy<M, P>(positionEvaluator), numWorkers, playerName + "_MM", msPerMove));
			playerMap.put(playerName + "_AB", () -> new ComputerPlayer(new AlphaBetaStrategy<M, P>(positionEvaluator), numWorkers, playerName + "_AB", msPerMove));
			return this;
		}
	}
}
