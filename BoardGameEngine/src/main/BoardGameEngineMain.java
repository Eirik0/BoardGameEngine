package main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import analysis.strategy.AlphaBetaStrategy;
import game.GameRunner;
import game.chess.ChessGame;
import game.chess.ChessGameRenderer;
import game.chess.ChessPositionEvaluator;
import game.forkjoinexample.ForkJoinExampleGame;
import game.forkjoinexample.ForkJoinExampleGameRenderer;
import game.forkjoinexample.ForkJoinExampleStraregy;
import game.gomoku.GomokuGame;
import game.gomoku.GomokuGameRenderer;
import game.gomoku.GomokuMoveList;
import game.gomoku.GomokuPositionEvaluator;
import game.sudoku.SudokuGame;
import game.sudoku.SudokuGameRenderer;
import game.sudoku.SudokuPositionEvaluator;
import game.tictactoe.TicTacToeGame;
import game.tictactoe.TicTacToeGameRenderer;
import game.tictactoe.TicTacToePositionEvaluator;
import game.ultimatetictactoe.UltimateTicTacToeGame;
import game.ultimatetictactoe.UltimateTicTacToeGameRenderer;
import game.ultimatetictactoe.UltimateTicTacToePositionEvaluator;
import gui.FixedDurationGameLoop;
import gui.GameGuiManager;
import gui.GameRegistry;
import gui.gamestate.GameRunningState;
import gui.gamestate.MainMenuState;

public class BoardGameEngineMain {
	private static final String TITLE = "Board Game Engine";
	private static final boolean DARK_THEME = true;

	public static final int DEFAULT_WIDTH = 1280;
	public static final int DEFAULT_HEIGHT = 720;

	public static final Font DEFAULT_FONT = new Font("consolas", Font.PLAIN, 24);
	public static final Font DEFAULT_FONT_SMALL = new Font(Font.DIALOG, Font.PLAIN, 12);

	public static final Color BACKGROUND_COLOR = DARK_THEME ? Color.BLACK : Color.WHITE;
	public static final Color FOREGROUND_COLOR = DARK_THEME ? Color.WHITE : Color.BLACK;
	public static final Color LIGHTER_FOREGROUND_COLOR = DARK_THEME ? new Color(200, 200, 200) : Color.GRAY;

	public static void main(String[] args) {
		registerGames();

		JFrame mainFrame = createMainFrame();
		MainPanel mainPanel = new MainPanel(mainFrame);
		mainFrame.setContentPane(mainPanel);

		GameGuiManager.setLoadGameAction(gameName -> mainPanel.loadGame(gameName));

		GameGuiManager.setGameState(new MainMenuState());

		mainFrame.pack();

		mainPanel.gamePanel.addToGameLoop("Game");
		FixedDurationGameLoop.startLoop();

		SwingUtilities.invokeLater(() -> {
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setVisible(true);
			mainPanel.gamePanel.requestFocus();
		});
	}

	private static void registerGames() {
		int defaultMaxWorkers = Runtime.getRuntime().availableProcessors() - 1;

		GameRegistry.registerGame(new ChessGame(), ChessGameRenderer.class)
				.registerHuman()
				.registerComputer(6000, defaultMaxWorkers)
				.registerMinimaxStrategies(new ChessPositionEvaluator());

		GameRegistry.registerGame(new TicTacToeGame(), TicTacToeGameRenderer.class)
				.registerHuman()
				.registerComputer(500, defaultMaxWorkers)
				.registerMinimaxStrategies(new TicTacToePositionEvaluator());

		GameRegistry.registerGame(new UltimateTicTacToeGame(), UltimateTicTacToeGameRenderer.class)
				.registerHuman()
				.registerComputer(3000, defaultMaxWorkers)
				.registerMinimaxStrategies(new UltimateTicTacToePositionEvaluator());

		GameRegistry.registerGame(new GomokuGame(), GomokuGameRenderer.class, GomokuMoveList.class)
				.registerHuman()
				.registerComputer(6000, defaultMaxWorkers)
				.registerMinimaxStrategies(new GomokuPositionEvaluator());

		GameRegistry.registerGame(new SudokuGame(), SudokuGameRenderer.class)
				.registerComputer(5000, defaultMaxWorkers)
				.registerStrategy("AlphaBeta", () -> new AlphaBetaStrategy<>(GameRegistry.getMoveListFactory(SudokuGame.NAME), new SudokuPositionEvaluator()));

		GameRegistry.registerGame(new ForkJoinExampleGame(), ForkJoinExampleGameRenderer.class)
				.registerComputer(Long.MAX_VALUE, 100)
				.registerStrategy("ForkJoinExample", () -> new ForkJoinExampleStraregy(GameRegistry.getMoveListFactory(ForkJoinExampleGame.NAME)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setGameState(String gameName, GameRunner<?, ?> gameRunner) {
		GameGuiManager.setGameState(new GameRunningState(gameRunner, GameRegistry.newGameRenderer(gameName)));
	}

	private static JFrame createMainFrame() {
		JFrame mainFrame = new JFrame(TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setFocusable(false);
		return mainFrame;
	}

	public static <T extends JComponent> T initComponent(T component) {
		component.setBackground(BACKGROUND_COLOR);
		component.setForeground(FOREGROUND_COLOR);
		component.setFocusable(false);
		return component;
	}
}
