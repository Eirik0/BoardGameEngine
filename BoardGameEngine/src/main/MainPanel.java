package main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import game.GameObserver;
import game.GameRunner;
import game.IGame;
import game.IPosition;
import game.MoveListFactory;
import gui.GameGuiManager;
import gui.GameMouseAdapter;
import gui.GamePanel;
import gui.GameRegistry;
import gui.analysis.AnalysisPanel;
import gui.movehistory.MoveHistoryPanel;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private final JFrame mainFrame;
	public final GamePanel gamePanel;

	public MainPanel(JFrame mainFrame) {
		super(new BorderLayout());
		setBackground(BoardGameEngineMain.BACKGROUND_COLOR);
		this.mainFrame = mainFrame;

		gamePanel = new GamePanel(g -> GameGuiManager.getGameState().drawOn(g), (width, height) -> GameGuiManager.setComponentSize(width.intValue(), height.intValue()));

		gamePanel.setPreferredSize(new Dimension(BoardGameEngineMain.DEFAULT_WIDTH, BoardGameEngineMain.DEFAULT_HEIGHT));

		GameMouseAdapter mouseAdapter = new GameMouseAdapter(GameGuiManager.getMouseTracker());
		gamePanel.addMouseMotionListener(mouseAdapter);
		gamePanel.addMouseListener(mouseAdapter);

		add(gamePanel, BorderLayout.CENTER);
	}

	public void loadGame(String gameName) {
		GameRunnerPanels<?, ?> gameRunnerPanels = new GameRunnerPanels<>(gameName);

		BoardGameEngineMain.setGameState(gameName, gameRunnerPanels.gameRunner);

		JSplitPane analysisSplitPane = createSplitPane(gamePanel, gameRunnerPanels.analysisPanel, GameGuiManager.getComponentWidth() * 15 / 24, 1);
		JSplitPane gameSplitPane = createSplitPane(gameRunnerPanels.moveHistoryPanel, analysisSplitPane, GameGuiManager.getComponentWidth() / 6, 0);

		gameRunnerPanels.playerControllerPanel.setBackAction(() -> {
			gameRunnerPanels.analysisPanel.stopAnalysis();
			gameRunnerPanels.moveHistoryPanel.stopDrawing();
			SwingUtilities.invokeLater(() -> {
				Dimension gamePanelSize = getSize();
				remove(gameRunnerPanels.playerControllerPanel);
				remove(gameSplitPane);
				add(gamePanel, BorderLayout.CENTER);
				gamePanel.setSize(gamePanelSize.getSize());
				repackFrame(mainFrame, gamePanel);
			});
		});

		SwingUtilities.invokeLater(() -> {
			Dimension splitPaneSize = new Dimension(getSize().width, getSize().height - gameRunnerPanels.playerControllerPanel.getPreferredSize().height);
			remove(gamePanel);
			add(gameSplitPane, BorderLayout.CENTER);
			add(gameRunnerPanels.playerControllerPanel, BorderLayout.NORTH);
			gameSplitPane.setSize(splitPaneSize);
			repackFrame(mainFrame, gameSplitPane);
			gameRunnerPanels.moveHistoryPanel.startDrawing();
			gameRunnerPanels.analysisPanel.startDrawing();
		});
	}

	private static JSplitPane createSplitPane(JComponent left, JComponent right, int dividerLocation, double resizeWeight) {
		JSplitPane splitPane = BoardGameEngineMain.initComponent(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right));
		splitPane.setDividerSize(3);
		splitPane.setDividerLocation(dividerLocation);
		splitPane.setResizeWeight(resizeWeight);
		return splitPane;
	}

	private static void repackFrame(JFrame mainFrame, JComponent gamePanel) {
		gamePanel.setPreferredSize(gamePanel.getSize());
		mainFrame.pack();
	}

	static class GameRunnerPanels<M, P extends IPosition<M, P>> {
		final AnalysisPanel<M, P> analysisPanel;
		final MoveHistoryPanel<M, P> moveHistoryPanel;
		final PlayerControllerPanel playerControllerPanel;
		final GameRunner<M, P> gameRunner;

		public GameRunnerPanels(String gameName) {
			IGame<M, P> game = GameRegistry.getGame(gameName);
			MoveListFactory<M> moveListFactory = GameRegistry.getMoveListFactory(game.getName());

			moveHistoryPanel = new MoveHistoryPanel<>();
			analysisPanel = new AnalysisPanel<>(gameName);

			GameObserver<M, P> gameObserver = new GameObserver<>();
			gameObserver.setPositionChangedAction(positionChangedInfo -> {
				moveHistoryPanel.setMoveHistory(positionChangedInfo.moveHistory);
				analysisPanel.positionChanged(positionChangedInfo);
			});

			gameRunner = new GameRunner<>(game, gameObserver, moveListFactory);
			playerControllerPanel = new PlayerControllerPanel(game, gameRunner);
			moveHistoryPanel.setGameRunner(gameRunner);

			gameObserver.setGameRunningAction(() -> playerControllerPanel.gameStarted());

			gameObserver.setGameStoppedAction(() -> {
				playerControllerPanel.gameEnded();
				analysisPanel.gameStopped();
			});
		}
	}
}
