package main;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import analysis.strategy.AlphaBetaQStrategy;
import analysis.strategy.MoveListProvider;
import game.GameRunner;
import game.chess.ChessGame;
import game.chess.ChessGameRenderer;
import game.chess.ChessPositionEvaluator;
import game.forkjoinexample.ForkJoinExampleGame;
import game.forkjoinexample.ForkJoinExampleGameRenderer;
import game.forkjoinexample.ForkJoinExampleStraregy;
import game.forkjoinexample.ForkJoinMoveList;
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
	public static final Font DEFAULT_SMALL_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
	public static final int DEFAULT_SMALL_FONT_HEIGHT = 18;

	public static final Color BACKGROUND_COLOR = DARK_THEME ? Color.BLACK : Color.WHITE;
	public static final Color FOREGROUND_COLOR = DARK_THEME ? Color.WHITE : Color.BLACK;
	public static final Color LIGHTER_FOREGROUND_COLOR = DARK_THEME ? new Color(200, 200, 200) : Color.GRAY;

	public static void main(String[] args) {
		registerGames();

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
		}

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
				.registerComputer(1000, defaultMaxWorkers)
				.registerStrategy("AlphaBetaQ", () -> new AlphaBetaQStrategy<>(new SudokuPositionEvaluator(), new MoveListProvider<>(GameRegistry.getMoveListFactory(SudokuGame.NAME))));

		GameRegistry.registerGame(new ForkJoinExampleGame(), ForkJoinExampleGameRenderer.class, ForkJoinMoveList.class)
				.registerComputer(Long.MAX_VALUE, 100)
				.registerStrategy("ForkJoinExample", () -> new ForkJoinExampleStraregy(new MoveListProvider<>(GameRegistry.getMoveListFactory(ForkJoinExampleGame.NAME))));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setGameState(String gameName, GameRunner<?, ?> gameRunner) {
		GameGuiManager.setGameState(new GameRunningState(gameRunner, GameRegistry.newGameRenderer(gameName)));
	}

	private static JFrame createMainFrame() {
		JFrame mainFrame = new JFrame(TITLE);
		mainFrame.setBackground(BoardGameEngineMain.BACKGROUND_COLOR);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setFocusable(false);
		return mainFrame;
	}

	public static <T extends JComponent> T initComponent(T component) {
		component.setBackground(BACKGROUND_COLOR);
		component.setForeground(FOREGROUND_COLOR);
		component.setFocusable(false);
		if (component instanceof PlayerControllerPanel) {
			component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(FOREGROUND_COLOR, 1), BorderFactory.createEmptyBorder(0, 10, 0, 0)));
		} else if (component instanceof JTextField) {
			component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createDashedBorder(null), BorderFactory.createEmptyBorder(2, 5, 2, 5)));
		} else if (!(component instanceof JButton)) {
			component.setBorder(BorderFactory.createEmptyBorder());
		}
		return component;
	}
}
