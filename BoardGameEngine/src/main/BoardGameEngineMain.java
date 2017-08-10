package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import game.GameRunner;
import game.IGame;
import game.tictactoe.TicTacToeGame;
import game.tictactoe.TicTacToeGameState;
import gui.FixedDurationGameLoop;
import gui.GameGuiManager;
import gui.GameImage;
import gui.GameMouseAdapter;
import gui.GamePanel;
import gui.GameRegistry;
import gui.MainMenuState;

public class BoardGameEngineMain {
	private static final String TITLE = "Board Game Engine";

	public static final int DEFAULT_WIDTH = 1024;
	public static final int DEFAULT_HEIGHT = 768;

	public static void main(String[] args) {
		registerGames();

		GameImage gameImage = new GameImage();

		JPanel contentPane = new JPanel(new BorderLayout());
		JFrame mainFrame = createMainFrame(contentPane);
		GamePanel gamePanel = createGamePanel(gameImage);

		contentPane.add(gamePanel, BorderLayout.CENTER);

		GameGuiManager.setSetGameAction(gameClass -> {
			IGame<?, ?> game = GameRegistry.newGame(gameClass);
			GameRunner<?, ?> gameRunner = new GameRunner<>(game);
			GameGuiManager.setGameState(GameRegistry.newGameState(gameClass, gameRunner));
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
				gameImage.checkResized(gamePanel);
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
		GameRegistry.registerGame(TicTacToeGame.class, TicTacToeGameState.class);
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
}
