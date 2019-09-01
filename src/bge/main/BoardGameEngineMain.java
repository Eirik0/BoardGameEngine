package bge.main;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import bge.analysis.IPositionEvaluator;
import bge.game.chess.ChessGame;
import bge.game.chess.ChessGameRenderer;
import bge.game.chess.ChessPositionEvaluator;
import bge.game.gomoku.GomokuGame;
import bge.game.gomoku.GomokuGameRenderer;
import bge.game.gomoku.GomokuPositionEvaluator;
import bge.game.papersoccer.PaperSoccerGame;
import bge.game.papersoccer.PaperSoccerGameRenderer;
import bge.game.papersoccer.PaperSoccerPositionEvaluator;
import bge.game.photosynthesis.PhotosynthesisGame;
import bge.game.photosynthesis.PhotosynthesisGameRenderer;
import bge.game.photosynthesis.PhotosynthesisPositionEvaluator;
import bge.game.tictactoe.TicTacToeGame;
import bge.game.tictactoe.TicTacToeGameRenderer;
import bge.game.tictactoe.TicTacToePositionEvaluator;
import bge.game.ultimatetictactoe.UTTTProbabilityPositionEvaluator;
import bge.game.ultimatetictactoe.UltimateTicTacToeGame;
import bge.game.ultimatetictactoe.UltimateTicTacToeGameRenderer;
import bge.game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;
import bge.gui.gamestate.IGameRenderer;
import bge.gui.gamestate.MainMenuState;
import bge.igame.IGame;
import bge.igame.IPosition;
import bge.igame.player.ComputerPlayer;
import bge.igame.player.PlayerInfo;
import bge.igame.player.PlayerOptions;
import bge.igame.player.PlayerOptions.CPOptionIntRange;
import bge.igame.player.PlayerOptions.CPOptionStringArray;
import bge.main.GameRegistry.GameRegistryItem;
import gt.component.ComponentCreator;
import gt.component.GamePanel;
import gt.component.IMouseTracker;
import gt.component.MainFrame;
import gt.gameentity.IGameImageDrawer;
import gt.gamestate.GameStateManager;
import gt.util.Pair;

public class BoardGameEngineMain {
    private static final String TITLE = "Board Game Engine";

    public static final Font DEFAULT_FONT = new Font("consolas", Font.PLAIN, 24);
    public static final Font DEFAULT_SMALL_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
    public static final int DEFAULT_SMALL_FONT_HEIGHT = 18;

    public static PlayerOptions createComputerPlayerOptions(IGame<?> game, int minMs, int maxMs, int maxThreads, int maxSimulations) {
        PlayerOptions msPerMoveOption = new PlayerOptions("time", new CPOptionIntRange(PlayerInfo.KEY_MS_PER_MOVE, minMs, maxMs));
        PlayerOptions threadOption = new PlayerOptions("threads", new CPOptionIntRange(PlayerInfo.KEY_NUM_THREADS, 1, maxThreads));
        PlayerOptions simulationsOption = new PlayerOptions("sims", new CPOptionIntRange(PlayerInfo.KEY_NUM_SIMULATIONS, 1, maxSimulations));
        PlayerOptions evaluatorOption = new PlayerOptions("Evaluator", new CPOptionStringArray(PlayerInfo.KEY_EVALUATOR,
                GameRegistry.getPositionEvaluatorNames(game.getName())));

        PlayerOptions fjStrategyOptions = new PlayerOptions("Strategy",
                new CPOptionStringArray(PlayerInfo.KEY_FJ_STRATEGY, PlayerInfo.ALL_FJ_STRATEGIES));
        for (String fjStrategy : PlayerInfo.ALL_FJ_STRATEGIES) {
            fjStrategyOptions.addSubOption(fjStrategy, evaluatorOption);
            fjStrategyOptions.addSubOption(fjStrategy, msPerMoveOption);
            fjStrategyOptions.addSubOption(fjStrategy, threadOption);
        }

        PlayerOptions mcStrategyOptions = new PlayerOptions("Strategy",
                new CPOptionStringArray(PlayerInfo.KEY_MC_STRATEGY, PlayerInfo.ALL_MC_STRATEGIES));
        for (String mcStrategy : PlayerInfo.ALL_MC_STRATEGIES) {
            mcStrategyOptions.addSubOption(mcStrategy, evaluatorOption);
            mcStrategyOptions.addSubOption(mcStrategy, msPerMoveOption);
            mcStrategyOptions.addSubOption(mcStrategy, simulationsOption);
        }

        return new PlayerOptions("Tree Searcher",
                new CPOptionStringArray(PlayerInfo.KEY_TS, PlayerInfo.ALL_TREE_SEARCHERS))
                        .addSubOption(PlayerInfo.TS_FORK_JOIN, fjStrategyOptions)
                        .addSubOption(PlayerInfo.TS_MONTE_CARLO, mcStrategyOptions);
    }

    private static <M, P extends IPosition<M>> void registerGame(IGame<M> game,
            List<Pair<String, IPositionEvaluator<M, P>>> positionEvaluators,
            int minMsPerMove, int maxMsPerMove, int maxThreads, int maxSimulations,
            BiFunction<IMouseTracker, IGameImageDrawer, IGameRenderer<M, P>> gameRendererSupplier) {
        GameRegistryItem<M, P> gameRegistryItem = GameRegistry.registerGame(game, gameRendererSupplier).addPlayer(ComputerPlayer.NAME);
        for (Pair<String, IPositionEvaluator<M, P>> nameEvaluator : positionEvaluators) {
            gameRegistryItem.addPositionEvaluator(nameEvaluator.getFirst(), nameEvaluator.getSecond());
        }
        PlayerOptions computerPlayerOptions = createComputerPlayerOptions(game, minMsPerMove, maxMsPerMove, maxThreads, maxSimulations);
        gameRegistryItem.setPlayerOptions(ComputerPlayer.NAME, computerPlayerOptions);
    }

    public static void registerGames() {
        int maxThreads = Runtime.getRuntime().availableProcessors() - 1;

        // TODO ChessConstants.MAX_REASONABLE_DEPTH, etc
        registerGame(new ChessGame(),
                Collections.singletonList(Pair.valueOf("Evaluator1", new ChessPositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new ChessGameRenderer(mouseTracker, imageDrawer));

        registerGame(new TicTacToeGame(),
                Collections.singletonList(Pair.valueOf("Evaluator1", new TicTacToePositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new TicTacToeGameRenderer(mouseTracker));

        registerGame(new UltimateTicTacToeGame(),
                Arrays.asList(Pair.valueOf("Evaluator1", new UltimateTicTacToePositionEvaluator()),
                        Pair.valueOf("Evaluator2", new UTTTProbabilityPositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new UltimateTicTacToeGameRenderer(mouseTracker));

        // TODO GomokuMoveList.class
        registerGame(new GomokuGame(),
                Collections.singletonList(Pair.valueOf("Evaluator1", new GomokuPositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new GomokuGameRenderer(mouseTracker));

        registerGame(new PaperSoccerGame(),
                Collections.singletonList(Pair.valueOf("Evaluator1", new PaperSoccerPositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new PaperSoccerGameRenderer(mouseTracker));

        registerGame(new PhotosynthesisGame(),
                Collections.singletonList(Pair.valueOf("Evaluator1", new PhotosynthesisPositionEvaluator())),
                50, 10000, maxThreads, 20,
                (mouseTracker, imageDrawer) -> new PhotosynthesisGameRenderer(mouseTracker));

        // TODO Sodoku
    }

    public static void main(String[] args) {
        registerGames();

        ComponentCreator.setCrossPlatformLookAndFeel();

        GamePanel mainPanel = new GamePanel("BGE");
        mainPanel.setPreferredSize(new Dimension(ComponentCreator.DEFAULT_WIDTH, ComponentCreator.DEFAULT_HEIGHT));

        GameStateManager gameStateManager = mainPanel.getGameStateManager();
        gameStateManager.setGameState(new MainMenuState(gameStateManager));

        MainFrame mainFrame = new MainFrame(TITLE, mainPanel);

        mainFrame.show();
    }
}
