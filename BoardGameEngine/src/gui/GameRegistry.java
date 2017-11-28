package gui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import analysis.strategy.IDepthBasedStrategy;
import game.ArrayMoveList;
import game.IGame;
import game.IPlayer;
import game.IPosition;
import game.MoveList;
import game.MoveListFactory;
import gui.gamestate.IGameRenderer;

public class GameRegistry {
	private static final Map<String, GameRegistryItem<?, ?>> gameMap = new LinkedHashMap<>();

	@SuppressWarnings("unchecked")
	public static <M, P extends IPosition<M, P>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
		return registerGame(game, gameRendererClass, (Class<? extends MoveList<M>>) ArrayMoveList.class);
	}

	public static <M, P extends IPosition<M, P>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass,
			Class<? extends MoveList<M>> moveListClass) {
		GameRegistryItem<M, P> gameRegistryItem = new GameRegistryItem<>(game, gameRendererClass, moveListClass);
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

	@SuppressWarnings("unchecked")
	public static <M> MoveListFactory<M> getMoveListFactory(String gameName) {
		return (MoveListFactory<M>) gameMap.get(gameName).moveListFactory;
	}

	public static Set<String> getPlayerNames(String gameName) {
		return gameMap.get(gameName).playerMap.keySet();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IPlayer getPlayer(String gameName, String playerName, ComputerPlayerInfo computerPlayerInfo) {
		return gameMap.get(gameName).playerMap.get(playerName).apply(computerPlayerInfo);
	}

	public static <M, P extends IPosition<M, P>> ComputerPlayerInfo<M, P> newDefaultComputerPlayerInfo(String gameName) {
		@SuppressWarnings("unchecked")
		GameRegistryItem<M, P> gameRegistryItem = (GameRegistryItem<M, P>) gameMap.get(gameName);
		int numWorkers = Math.min(Math.max(1, gameRegistryItem.maxWorkers), 4);
		Entry<String, Supplier<IDepthBasedStrategy<M, P>>> strategySupplier = gameRegistryItem.strategySupplierMap.entrySet().iterator().next();
		return new ComputerPlayerInfo<>(strategySupplier.getKey(), strategySupplier.getValue(), numWorkers, gameRegistryItem.defaultMsPerMove, gameRegistryItem.maxWorkers);
	}

	public static Set<String> getStrategyNames(String gameName) {
		return gameMap.get(gameName).strategySupplierMap.keySet();
	}

	public static <M, P extends IPosition<M, P>> void updateComputerPlayerInfo(ComputerPlayerInfo<M, P> infoToUpdate, String gameName, String strategyName, int numWorkers,
			long msPerMove) {
		@SuppressWarnings("unchecked")
		GameRegistryItem<M, P> gameRegistryItem = (GameRegistryItem<M, P>) gameMap.get(gameName);
		Entry<String, Supplier<IDepthBasedStrategy<M, P>>> strategySupplier = gameRegistryItem.strategySupplierMap.entrySet().iterator().next();
		infoToUpdate.setValues(strategySupplier.getKey(), strategySupplier.getValue(), numWorkers, msPerMove);
	}

	public static <M, P extends IPosition<M, P>> Supplier<IDepthBasedStrategy<M, P>> getStrategySupplier(String gameName, String strategyName) {
		@SuppressWarnings("unchecked")
		GameRegistryItem<M, P> gameRegistryItem = (GameRegistryItem<M, P>) gameMap.get(gameName);
		return gameRegistryItem.strategySupplierMap.get(strategyName);
	}

	public static class GameRegistryItem<M, P extends IPosition<M, P>> {
		private final IGame<M, P> game;
		final Class<? extends IGameRenderer<M, P>> gameRendererClass;
		final Map<String, Function<ComputerPlayerInfo<M, P>, IPlayer>> playerMap = new LinkedHashMap<>(); // The first player is the default player
		final Class<? extends MoveList<M>> analysisMoveListClass;;
		final MoveListFactory<M> moveListFactory;
		final Map<String, Supplier<IDepthBasedStrategy<M, P>>> strategySupplierMap = new LinkedHashMap<>();
		long defaultMsPerMove = 3000;
		int maxWorkers = 1;

		public GameRegistryItem(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass, Class<? extends MoveList<M>> analysisMoveListClass) {
			this.game = game;
			this.gameRendererClass = gameRendererClass;
			this.analysisMoveListClass = analysisMoveListClass;
			moveListFactory = new MoveListFactory<>(game.getMaxMoves(), analysisMoveListClass);
		}

		public GameRegistryItem<M, P> registerHuman() {
			playerMap.put(GuiPlayer.NAME, info -> GuiPlayer.HUMAN);
			return this;
		}

		public GameRegistryItem<M, P> registerComputer(long defaultMsPerMove, int maxWorkers) {
			playerMap.put(ComputerPlayer.NAME, info -> new ComputerPlayer(info.strategyName, info.strategySupplier.get(), moveListFactory, info.numWorkers, info.msPerMove));
			this.defaultMsPerMove = defaultMsPerMove;
			this.maxWorkers = maxWorkers;
			return this;
		}

		public GameRegistryItem<M, P> registerStrategy(String stratrgyName, Supplier<IDepthBasedStrategy<M, P>> strategySupplier) {
			strategySupplierMap.put(stratrgyName, strategySupplier);
			return this;
		}
	}
}
