package bge.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import bge.analysis.IPositionEvaluator;
import bge.gui.gamestate.IGameRenderer;
import bge.igame.ArrayMoveList;
import bge.igame.IGame;
import bge.igame.IPosition;
import bge.igame.MoveList;
import bge.igame.MoveListFactory;
import bge.igame.player.GuiPlayer;
import bge.igame.player.PlayerOptions;
import gt.component.IMouseTracker;
import gt.gameentity.IGameImageDrawer;

public class GameRegistry {
    private static final Map<String, GameRegistryItem<?, ?>> gameMap = new LinkedHashMap<>();

    @SuppressWarnings("rawtypes")
    public static <M, P extends IPosition<M>> GameRegistryItem<M, P> registerGame(IGame<M, P> game,
            BiFunction<IMouseTracker, IGameImageDrawer, IGameRenderer<M, P>> gameRendererSupplier) {
        return registerGame(game, (Class<? extends MoveList>) ArrayMoveList.class, gameRendererSupplier);
    }

    @SuppressWarnings("rawtypes")
    public static <M, P extends IPosition<M>> GameRegistryItem<M, P> registerGame(IGame<M, P> game, Class<? extends MoveList> moveListClass,
            BiFunction<IMouseTracker, IGameImageDrawer, IGameRenderer<M, P>> gameRendererSupplier) {
        GameRegistryItem<M, P> gameRegistryItem = new GameRegistryItem<>(game, moveListClass, gameRendererSupplier);
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
    public static <M> IGameRenderer<M, IPosition<M>> getGameRenderer(String gameName, IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        return (IGameRenderer<M, IPosition<M>>) gameMap.get(gameName).gameRendererSupplier.apply(mouseTracker, imageDrawer);
    }

    @SuppressWarnings("unchecked")
    public static <M> MoveListFactory<M> getMoveListFactory(String gameName) {
        return (MoveListFactory<M>) gameMap.get(gameName).moveListFactory;
    }

    public static String[] getPlayerNames(String gameName) {
        return gameMap.get(gameName).playerNames.toArray(new String[0]);
    }

    public static String[] getPositionEvaluatorNames(String gameName) {
        return gameMap.get(gameName).positionEvaluators.keySet().toArray(new String[0]);
    }

    public static PlayerOptions getPlayerOptions(String gameName, String playerName) {
        return gameMap.get(gameName).playerOptions.get(playerName);
    }

    @SuppressWarnings("unchecked")
    public static <M> IPositionEvaluator<M, IPosition<M>> getPositionEvaluator(String gameName, String evaluatorName) {
        return (IPositionEvaluator<M, IPosition<M>>) gameMap.get(gameName).positionEvaluators.get(evaluatorName);
    }

    public static class GameRegistryItem<M, P extends IPosition<M>> {
        final IGame<M, P> game;
        final BiFunction<IMouseTracker, IGameImageDrawer, IGameRenderer<M, P>> gameRendererSupplier;
        final MoveListFactory<M> moveListFactory;
        final List<String> playerNames = new ArrayList<>();
        final Map<String, PlayerOptions> playerOptions = new HashMap<>();
        final Map<String, IPositionEvaluator<M, P>> positionEvaluators = new LinkedHashMap<>();

        @SuppressWarnings("rawtypes")
        public GameRegistryItem(IGame<M, P> game, Class<? extends MoveList> analysisMoveListClass,
                BiFunction<IMouseTracker, IGameImageDrawer, IGameRenderer<M, P>> gameRendererSupplier) {
            this.game = game;
            this.gameRendererSupplier = gameRendererSupplier;
            moveListFactory = new MoveListFactory<>(game.getMaxMoves(), analysisMoveListClass);
            playerNames.add(GuiPlayer.NAME);
        }

        public GameRegistryItem<M, P> removeHumanPlayer() {
            playerNames.remove(0);
            return this;
        }

        public GameRegistryItem<M, P> addPlayer(String name) {
            playerNames.add(name);
            return this;
        }

        public GameRegistryItem<M, P> addPositionEvaluator(String name, IPositionEvaluator<M, P> evaluator) {
            positionEvaluators.put(name, evaluator);
            return this;
        }

        public GameRegistryItem<M, P> setPlayerOptions(String name, PlayerOptions options) {
            playerOptions.put(name, options);
            return this;
        }
    }
}
