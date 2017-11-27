package gui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import analysis.ComputerPlayer;
import analysis.IPositionEvaluator;
import analysis.strategy.AlphaBetaStrategy;
import analysis.strategy.IDepthBasedStrategy;
import analysis.strategy.MinimaxStrategy;
import game.ArrayMoveList;
import game.IGame;
import game.IPlayer;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import gui.gamestate.IGameRenderer;

public class GameRegistry {
	private static final Map<String, GameRegistryItem<?, ?>> gameMap = new LinkedHashMap<>();

	public static <M, P extends IPosition<M, P>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
		GameRegistryItem<M, P> gameRegistryItem = new GameRegistryItem<>(game, gameRendererClass);
		gameMap.put(game.getName(), gameRegistryItem);
		return gameRegistryItem;
	}

	public static Set<String> getGameNames() {
		return gameMap.keySet();
	}

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> IGame<M, P> getGame(String gameName) {
		return (IGame<M, P>) gameMap.get(gameName).game;
	}

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> IGameRenderer<M, P> newGameRenderer(String gameName) {
		try {
			return (IGameRenderer<M, P>) gameMap.get(gameName).gameRendererClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <M> MoveListFactory<M> newMoveListFactory(String gameName) {
		GameRegistryItem<?, ?> gameRegistryItem = gameMap.get(gameName);
		return new MoveListFactory(gameRegistryItem.game.getMaxMoves(), gameRegistryItem.analysisMoveListClass);
	}

	public static Set<String> getPlayerNames(String gameName) {
		return gameMap.get(gameName).playerMap.keySet();
	}

	public static IPlayer getPlayer(String gameName, String playerName) {
		return gameMap.get(gameName).playerMap.get(playerName).get();
	}

	public static class GameRegistryItem<M, P extends IPosition<M, P>> {
		private final IGame<M, P> game;
		final Class<? extends IGameRenderer<?, ?>> gameRendererClass;
		final Map<String, Supplier<IPlayer>> playerMap = new LinkedHashMap<>(); // The first player is the default player
		@SuppressWarnings("unchecked")
		Class<? extends MoveList<M>> analysisMoveListClass = (Class<? extends MoveList<M>>) ArrayMoveList.class;

		public GameRegistryItem(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
			this.game = game;
			this.gameRendererClass = gameRendererClass;
		}

		public GameRegistryItem<M, P> registerPlayer(String playerName, IPlayer player) {
			playerMap.put(playerName, () -> player);
			return this;
		}

		public GameRegistryItem<M, P> registerStrategy(String playerName, IDepthBasedStrategy<M, P> strategy, int numWorkers, long msPerMove) {
			MoveListFactory<?> moveListFactory = GameRegistry.newMoveListFactory(game.getName());
			playerMap.put(playerName, () -> new ComputerPlayer(strategy, moveListFactory, numWorkers, playerName, msPerMove));
			return this;
		}

		public GameRegistryItem<M, P> registerPositionEvaluator(String playerName, IPositionEvaluator<M, P> positionEvaluator, int numWorkers, long msPerMove) {
			MoveListFactory<M> moveListFactory = GameRegistry.newMoveListFactory(game.getName());
			playerMap.put(playerName + "_AB", () -> new ComputerPlayer(new AlphaBetaStrategy<>(moveListFactory, positionEvaluator), moveListFactory, numWorkers, playerName + "_AB", msPerMove));
			playerMap.put(playerName + "_MM", () -> new ComputerPlayer(new MinimaxStrategy<>(moveListFactory, positionEvaluator), moveListFactory, numWorkers, playerName + "_MM", msPerMove));
			return this;
		}

		public GameRegistryItem<M, P> registerAnalysisMoveListClass(Class<? extends MoveList<M>> moveListClass) {
			analysisMoveListClass = moveListClass;
			return this;
		}
	}
}
