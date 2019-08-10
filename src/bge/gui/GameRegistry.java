package bge.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import bge.analysis.ComputerPlayer;
import bge.analysis.ComputerPlayerInfo;
import bge.analysis.IPositionEvaluator;
import bge.analysis.ITreeSearcher;
import bge.analysis.montecarlo.MonteCarloTreeSearcher;
import bge.analysis.montecarlo.RandomMonteCarloChildren;
import bge.analysis.montecarlo.WeightedMonteCarloChildren;
import bge.analysis.search.IterativeDeepeningTreeSearcher;
import bge.analysis.strategy.AlphaBetaQStrategy;
import bge.analysis.strategy.AlphaBetaStrategy;
import bge.analysis.strategy.MinimaxStrategy;
import bge.analysis.strategy.MoveListProvider;
import bge.game.ArrayMoveList;
import bge.game.IGame;
import bge.game.IPlayer;
import bge.game.IPosition;
import bge.game.MoveList;
import bge.game.MoveListFactory;
import bge.gui.gamestate.IGameRenderer;

public class GameRegistry {
    private static final Map<String, GameRegistryItem<?, ?>> gameMap = new LinkedHashMap<>();

    @SuppressWarnings({ "rawtypes" })
    public static <M, P extends IPosition<M>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass) {
        return registerGame(game, gameRendererClass, (Class<? extends MoveList>) ArrayMoveList.class);
    }

    public static <M, P extends IPosition<M>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass,
            @SuppressWarnings("rawtypes") Class<? extends MoveList> moveListClass) {
        GameRegistryItem<M, P> gameRegistryItem = new GameRegistryItem<>(game, gameRendererClass, moveListClass);
        gameMap.put(game.getName(), gameRegistryItem);
        return gameRegistryItem;
    }

    public static Set<String> getGameNames() {
        return gameMap.keySet();
    }

    @SuppressWarnings("unchecked")
    public static <M, P extends IPosition<M>> IGame<M, P> getGame(String gameName) {
        return (IGame<M, P>) gameMap.get(gameName).game;
    }

    @SuppressWarnings("unchecked")
    public static <M, P extends IPosition<M>> IGameRenderer<M, P> newGameRenderer(String gameName) {
        try {
            return (IGameRenderer<M, P>) gameMap.get(gameName).gameRendererClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
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

    public static <M, P extends IPosition<M>> ComputerPlayerInfo<M, P> newDefaultComputerPlayerInfo(String gameName) {
        @SuppressWarnings("unchecked")
        GameRegistryItem<M, P> gameRegistryItem = (GameRegistryItem<M, P>) gameMap.get(gameName);
        int numWorkers = Math.min(Math.max(1, gameRegistryItem.maxWorkers), 4);
        Entry<String, Function<ComputerPlayerInfo<M, P>, ITreeSearcher<M, P>>> strategySupplier = gameRegistryItem.strategySupplierMap.entrySet().iterator()
                .next();
        return new ComputerPlayerInfo<>(strategySupplier.getKey(), strategySupplier.getValue(), numWorkers, gameRegistryItem.defaultMsPerMove,
                gameRegistryItem.maxWorkers);
    }

    public static Set<String> getStrategyNames(String gameName) {
        return gameMap.get(gameName).strategySupplierMap.keySet();
    }

    public static <M, P extends IPosition<M>> void updateComputerPlayerInfo(ComputerPlayerInfo<M, P> infoToUpdate, String gameName, String strategyName,
            int numWorkers, long msPerMove) {
        @SuppressWarnings("unchecked")
        GameRegistryItem<M, P> gameRegistryItem = (GameRegistryItem<M, P>) gameMap.get(gameName);
        infoToUpdate.setValues(strategyName, gameRegistryItem.strategySupplierMap.get(strategyName), numWorkers, msPerMove);
    }

    public static class GameRegistryItem<M, P extends IPosition<M>> {
        final IGame<M, P> game;
        final Class<? extends IGameRenderer<M, P>> gameRendererClass;
        final MoveListFactory<M> moveListFactory;
        final Map<String, Function<ComputerPlayerInfo<M, P>, IPlayer>> playerMap = new LinkedHashMap<>(); // The first player is the default player
        final Map<String, Function<ComputerPlayerInfo<M, P>, ITreeSearcher<M, P>>> strategySupplierMap = new LinkedHashMap<>();
        long defaultMsPerMove = 3000;
        int maxWorkers = 1;

        public GameRegistryItem(IGame<M, P> game, Class<? extends IGameRenderer<M, P>> gameRendererClass,
                @SuppressWarnings("rawtypes") Class<? extends MoveList> analysisMoveListClass) {
            this.game = game;
            this.gameRendererClass = gameRendererClass;
            moveListFactory = new MoveListFactory<>(game.getMaxMoves(), analysisMoveListClass);
        }

        public GameRegistryItem<M, P> registerHuman() {
            playerMap.put(GuiPlayer.NAME, info -> GuiPlayer.HUMAN);
            return this;
        }

        public GameRegistryItem<M, P> registerComputer(long defaultMsPerMove, int maxWorkers) {
            playerMap.put(ComputerPlayer.NAME,
                    info -> new ComputerPlayer(info.strategyName, info.strategySupplier.apply(info), info.numWorkers, info.msPerMove, !info.infiniteTimeOnly));
            this.defaultMsPerMove = defaultMsPerMove;
            this.maxWorkers = maxWorkers;
            return this;
        }

        public GameRegistryItem<M, P> registerTreeSearcher(String stratrgyName, Function<ComputerPlayerInfo<M, P>, ITreeSearcher<M, P>> strategySupplier) {
            strategySupplierMap.put(stratrgyName, strategySupplier);
            return this;
        }

        public GameRegistryItem<M, P> registerMinimaxStrategies(IPositionEvaluator<M, P> positionEvaluator, boolean quiescent) {
            return registerMinimaxStrategies(positionEvaluator, null, quiescent);
        }

        public GameRegistryItem<M, P> registerMinimaxStrategies(IPositionEvaluator<M, P> positionEvaluator, String name, boolean quiescent) {
            if (quiescent) {
                registerTreeSearcher("AlphaBetaQ" + (name == null ? "" : "_" + name),
                        info -> new IterativeDeepeningTreeSearcher<>(new AlphaBetaQStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory)),
                                moveListFactory, info.numWorkers));
            }
            registerTreeSearcher("AlphaBeta" + (name == null ? "" : "_" + name),
                    info -> new IterativeDeepeningTreeSearcher<>(new AlphaBetaStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory)),
                            moveListFactory, info.numWorkers));
            registerTreeSearcher("MinMax" + (name == null ? "" : "_" + name),
                    info -> new IterativeDeepeningTreeSearcher<>(new MinimaxStrategy<>(positionEvaluator, new MoveListProvider<>(moveListFactory)),
                            moveListFactory, info.numWorkers));
            return this;
        }

        public GameRegistryItem<M, P> registerMonteCarloStrategy(IPositionEvaluator<M, P> positionEvaluator, int numSimluations, int maxDepth) {
            registerTreeSearcher("MonteCarlo",
                    info -> new MonteCarloTreeSearcher<>(new RandomMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimluations, maxDepth));
            registerTreeSearcher("MonteCarloW",
                    info -> new MonteCarloTreeSearcher<>(new WeightedMonteCarloChildren<>(0), positionEvaluator, moveListFactory, numSimluations, maxDepth));
            return this;
        }
    }
}
