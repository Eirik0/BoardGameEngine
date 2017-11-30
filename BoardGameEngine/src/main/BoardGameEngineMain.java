package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import analysis.strategy.AlphaBetaStrategy;
import game.GameObserver;
import game.GameRunner;
import game.IGame;
import game.MoveListFactory;
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
import gui.GameImage;
import gui.GameMouseAdapter;
import gui.GamePanel;
import gui.GameRegistry;
import gui.analysis.AnalysisPanel;
import gui.gamestate.GameRunningState;
import gui.gamestate.MainMenuState;

public class BoardGameEngineMain {
	private static final String TITLE = "Board Game Engine";
	private static final boolean DARK_THEME = true;

	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;

	public static final Font DEFAULT_FONT = new Font("consolas", Font.PLAIN, 24);
	public static final Font DEFAULT_FONT_SMALL = new Font(Font.DIALOG, Font.PLAIN, 12);

	public static final Color BACKGROUND_COLOR = DARK_THEME ? Color.BLACK : Color.WHITE;
	public static final Color FOREGROUND_COLOR = DARK_THEME ? Color.WHITE : Color.BLACK;
	public static final Color LIGHTER_FOREGROUND_COLOR = DARK_THEME ? Color.LIGHT_GRAY : Color.GRAY;

	public static void main(String[] args) {
		registerGames();

		GameImage gameImage = new GameImage();

		JPanel contentPane = new JPanel(new BorderLayout());
		JFrame mainFrame = createMainFrame(contentPane);
		GamePanel gamePanel = createGamePanel(gameImage);

		contentPane.add(gamePanel, BorderLayout.CENTER);

		GameGuiManager.setSetGameAction(gameName -> {
			IGame<?, ?> game = GameRegistry.getGame(gameName);
			MoveListFactory<?> moveListFactory = GameRegistry.getMoveListFactory(game.getName());

			AnalysisPanel<?, ?> analysisPanel = new AnalysisPanel<>(gameName);

			GameObserver<?, ?> gameObserver = new GameObserver<>();
			gameObserver.setPlayerChangedAction(analysisPanel::playerChanged);
			gameObserver.setPositionChangedAction(analysisPanel::positionChanged);
			gameObserver.setEndGameAction(analysisPanel::gameEnded);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			GameRunner<?, ?> gameRunner = new GameRunner(game, gameObserver, moveListFactory);

			PlayerControllerPanel playerControllerPanel = new PlayerControllerPanel(game, gameRunner);
			JSplitPane gameSplitPane = createSplitPane(gamePanel, analysisPanel);

			setGameState(gameName, gameRunner);

			playerControllerPanel.setBackAction(() -> {
				analysisPanel.stopDrawThread();

				SwingUtilities.invokeLater(() -> {
					Dimension gamePanelSize = contentPane.getSize();
					contentPane.remove(playerControllerPanel);
					contentPane.remove(gameSplitPane);
					contentPane.add(gamePanel, BorderLayout.CENTER);
					gamePanel.setSize(gamePanelSize.getSize());
					repackFrame(mainFrame, gamePanel);
				});
			});

			SwingUtilities.invokeLater(() -> {
				Dimension splitPaneSize = new Dimension(contentPane.getSize().width, contentPane.getSize().height - playerControllerPanel.getPreferredSize().height);
				contentPane.remove(gamePanel);
				contentPane.add(gameSplitPane, BorderLayout.CENTER);
				contentPane.add(playerControllerPanel, BorderLayout.NORTH);
				gameSplitPane.setSize(splitPaneSize);
				repackFrame(mainFrame, gameSplitPane);
			});
		});

		GameGuiManager.setGameState(new MainMenuState());

		FixedDurationGameLoop gameLoop = new FixedDurationGameLoop(() -> {
			GameGuiManager.getGameState().drawOn(gameImage.getGraphics());
			gamePanel.repaintAndWait();
		});

		gamePanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				gameImage.checkResized(gamePanel.getWidth(), gamePanel.getHeight());
				GameGuiManager.setComponentSize(gamePanel.getWidth(), gamePanel.getHeight());
			}
		});

		mainFrame.pack();

		new Thread(() -> gameLoop.runLoop(), "Game_Loop_Thread").start();

		SwingUtilities.invokeLater(() -> {
			mainFrame.setLocationRelativeTo(null);
			mainFrame.setVisible(true);
			gamePanel.requestFocus();
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
				.registerComputer(15000, defaultMaxWorkers)
				.registerStrategy("AlphaBeta", () -> new AlphaBetaStrategy<>(GameRegistry.getMoveListFactory(SudokuGame.NAME), new SudokuPositionEvaluator()));

		GameRegistry.registerGame(new ForkJoinExampleGame(), ForkJoinExampleGameRenderer.class)
				.registerComputer(Long.MAX_VALUE, 100)
				.registerStrategy("ForkJoinExample", () -> new ForkJoinExampleStraregy(GameRegistry.getMoveListFactory(ForkJoinExampleGame.NAME)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setGameState(String gameName, GameRunner<?, ?> gameRunner) {
		GameGuiManager.setGameState(new GameRunningState(gameRunner, GameRegistry.newGameRenderer(gameName)));
	}

	private static GamePanel createGamePanel(GameImage gameImage) {
		GamePanel gamePanel = new GamePanel(gameImage);
		gamePanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

		GameMouseAdapter mouseAdapter = new GameMouseAdapter();

		gamePanel.addMouseMotionListener(mouseAdapter);
		gamePanel.addMouseListener(mouseAdapter);

		return gamePanel;
	}

	private static JFrame createMainFrame(JPanel contentPane) {
		JFrame mainFrame = new JFrame(TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setFocusable(false);
		mainFrame.setContentPane(contentPane);
		return mainFrame;
	}

	private static JSplitPane createSplitPane(JComponent left, JComponent right) {
		JSplitPane splitPane = initComponent(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right));
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(GameGuiManager.getComponentWidth() * 3 / 4 - 30);
		splitPane.setResizeWeight(1);
		return splitPane;
	}

	private static void repackFrame(JFrame mainFrame, JComponent gamePanel) {
		gamePanel.setPreferredSize(gamePanel.getSize());
		mainFrame.pack();
	}

	public static <T extends JComponent> T initComponent(T component) {
		component.setBackground(BACKGROUND_COLOR);
		component.setForeground(FOREGROUND_COLOR);
		component.setFocusable(false);
		return component;
	}
}
