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
import javax.swing.SwingUtilities;

import game.GameRunner;
import game.IGame;
import game.chess.ChessGame;
import game.chess.ChessGameRenderer;
import game.forkjoinexample.ForkJoinExampleGame;
import game.forkjoinexample.ForkJoinExampleGameRenderer;
import game.forkjoinexample.ForkJoinExampleStraregy;
import game.gomoku.GomokuGame;
import game.gomoku.GomokuGameRenderer;
import game.gomoku.GomokuPositionEvaluator;
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
import gui.GuiPlayer;
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

	public static void main(String[] args) {
		registerGames();

		GameImage gameImage = new GameImage();

		JPanel contentPane = new JPanel(new BorderLayout());
		JFrame mainFrame = createMainFrame(contentPane);
		GamePanel gamePanel = createGamePanel(gameImage);

		contentPane.add(gamePanel, BorderLayout.CENTER);

		GameGuiManager.setSetGameAction(gameName -> {
			IGame<?, ?> game = GameRegistry.newGame(gameName);
			GameRunner<?, ?> gameRunner = new GameRunner<>(game);
			setGameState(gameName, gameRunner);
			PlayerControllerPanel playerControllerPanel = new PlayerControllerPanel(game, gameRunner);
			playerControllerPanel.setBackAction(() -> {
				contentPane.remove(playerControllerPanel);
				repackFrame(mainFrame, gamePanel);
			});
			contentPane.add(playerControllerPanel, BorderLayout.NORTH);
			repackFrame(mainFrame, gamePanel);
		});

		GameGuiManager.setGameState(new MainMenuState());
		FixedDurationGameLoop gameLoop = new FixedDurationGameLoop(gamePanel, gameImage);

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
			mainFrame.setVisible(true);
			gamePanel.requestFocus();
		});
	}

	private static void registerGames() {
		GameRegistry.registerGame(ChessGame.NAME, ChessGame.class, ChessGameRenderer.class)
				.registerPlayer(GuiPlayer.NAME, GuiPlayer.HUMAN);

		GameRegistry.registerGame(TicTacToeGame.NAME, TicTacToeGame.class, TicTacToeGameRenderer.class)
				.registerPlayer(GuiPlayer.NAME, GuiPlayer.HUMAN)
				.registerPositionEvaluator("Computer", new TicTacToePositionEvaluator(), 2, 500);

		GameRegistry.registerGame(UltimateTicTacToeGame.NAME, UltimateTicTacToeGame.class, UltimateTicTacToeGameRenderer.class)
				.registerPlayer(GuiPlayer.NAME, GuiPlayer.HUMAN)
				.registerPositionEvaluator("Computer 1", new UltimateTicTacToePositionEvaluator(), 2, 3000)
				.registerPositionEvaluator("Computer 2", new UltimateTicTacToePositionEvaluator(), 6, 12000);

		GameRegistry.registerGame(GomokuGame.NAME, GomokuGame.class, GomokuGameRenderer.class)
				.registerPlayer(GuiPlayer.NAME, GuiPlayer.HUMAN)
				.registerPositionEvaluator("Computer", new GomokuPositionEvaluator(), 4, 5000);

		GameRegistry.registerGame(ForkJoinExampleGame.NAME, ForkJoinExampleGame.class, ForkJoinExampleGameRenderer.class)
				.registerStrategy("1 Worker", new ForkJoinExampleStraregy(), 1, Long.MAX_VALUE)
				.registerStrategy("2 Workers", new ForkJoinExampleStraregy(), 2, Long.MAX_VALUE)
				.registerStrategy("3 Workers", new ForkJoinExampleStraregy(), 3, Long.MAX_VALUE)
				.registerStrategy("4 Workers", new ForkJoinExampleStraregy(), 4, Long.MAX_VALUE)
				.registerStrategy("5 Workers", new ForkJoinExampleStraregy(), 5, Long.MAX_VALUE)
				.registerStrategy("10 Workers", new ForkJoinExampleStraregy(), 10, Long.MAX_VALUE)
				.registerStrategy("37 Workers", new ForkJoinExampleStraregy(), 37, Long.MAX_VALUE);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void setGameState(String gameName, GameRunner<?, ?> gameRunner) {
		GameGuiManager.setGameState(new GameRunningState(gameRunner, GameRegistry.newGameRenderer(gameName)));
	}

	private static GamePanel createGamePanel(GameImage gameImage) {
		GamePanel gamePanel = new GamePanel(gameImage);
		gamePanel.setPreferredSize(new Dimension(DEFAULT_HEIGHT, DEFAULT_HEIGHT));

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

	private static void repackFrame(JFrame mainFrame, GamePanel gamePanel) {
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
