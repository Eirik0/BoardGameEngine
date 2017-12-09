package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import game.GameRunner;
import game.IGame;
import game.IPlayer;
import gui.GameGuiManager;
import gui.GameRegistry;
import gui.gamestate.MainMenuState;

@SuppressWarnings("serial")
public class PlayerControllerPanel extends JPanel {
	private String gameName;

	final JLabel gameLabel = BoardGameEngineMain.initComponent(new JLabel());
	private final List<PlayerSelectionPanel> playerSelectionPanels = new ArrayList<>();

	private PausePlayButton pausePlayButton;
	private Runnable backAction;

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);
		gameName = game.getName();
		gameLabel.setText(gameName);
		int numberOfPlayers = game.getNumberOfPlayers();
		String[] playerNames = GameRegistry.getPlayerNames(gameName).toArray(new String[0]);
		String[] availablePlayers = playerNames;
		for (int i = 0; i < numberOfPlayers; i++) {
			playerSelectionPanels.add(new PlayerSelectionPanel(availablePlayers, gameName));
		}
		rebuildWith(game, gameRunner);
	}

	public void setBackAction(Runnable backAction) { // this allows for easy self reference outside of this
		this.backAction = backAction;
	}

	private void rebuildWith(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		gameName = game.getName();
		JPanel buttonPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.CENTER)));
		buttonPanel.add(Box.createHorizontalStrut(30));
		for (int i = 0; i < playerSelectionPanels.size(); i++) {
			buttonPanel.add(playerSelectionPanels.get(i));
			if (i < playerSelectionPanels.size() - 1) {
				buttonPanel.add(BoardGameEngineMain.initComponent(new JLabel(" v. ")));
			}
		}

		JPanel buttonPanelWrapper = new JPanel(new BorderLayout());
		BoardGameEngineMain.initComponent(buttonPanelWrapper);
		buttonPanelWrapper.add(buttonPanel, BorderLayout.EAST);

		JButton newGameButton = BoardGameEngineMain.initComponent(new JButton("New Game"));
		JButton backButton = BoardGameEngineMain.initComponent(new JButton("Back"));
		pausePlayButton = new PausePlayButton(gameName, gameRunner, this, newGameButton, backButton);
		JButton[] buttons = new JButton[] { newGameButton, backButton, pausePlayButton };

		newGameButton.addActionListener(createEnableDisableRunnableWrapper(() -> gameRunner.createNewGame(), buttons));
		backButton.addActionListener(createEnableDisableRunnableWrapper(() -> {
			backAction.run();
			gameRunner.pauseGame(false);
			GameGuiManager.setGameState(new MainMenuState());
		}, buttons));

		buttonPanel.add(Box.createHorizontalStrut(30));
		buttonPanel.add(pausePlayButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(newGameButton);
		removeAll();
		add(gameLabel, BorderLayout.WEST);
		add(buttonPanelWrapper, BorderLayout.CENTER);
		add(backButton, BorderLayout.EAST);
	}

	public void gameStarted() {
		gameLabel.setText(gameName + "...");
		pausePlayButton.gameStarted();
	}

	public void gameEnded() {
		gameLabel.setText(gameName);
		pausePlayButton.gameEnded();
	}

	List<IPlayer> getSelectedPlayers() {
		List<IPlayer> players = new ArrayList<>();
		for (PlayerSelectionPanel playerSelectionPanel : playerSelectionPanels) {
			players.add(playerSelectionPanel.getPlayer(gameName));
		}
		return players;
	}

	static ActionListener createEnableDisableRunnableWrapper(Runnable r, JButton... buttons) {
		return e -> {
			for (JButton button : buttons) {
				button.setEnabled(false);
			}
			new Thread(() -> {
				try {
					r.run();
				} finally {
					for (JButton button : buttons) {
						button.setEnabled(true);
					}
				}
			}, "Enabke_Disable_Button_Thread").start();
		};
	}

	static class PausePlayButton extends JButton {
		private final AtomicBoolean paused;

		public PausePlayButton(String gameName, GameRunner<?, ?> gameRunner, PlayerControllerPanel playerControllerPanel, JButton newGameButton, JButton backButton) {
			super("  Play  ");
			paused = new AtomicBoolean(true);
			BoardGameEngineMain.initComponent(this);
			addActionListener(createEnableDisableRunnableWrapper(() -> {
				if (paused.getAndSet(!paused.get())) {
					gameRunner.setPlayers(playerControllerPanel.getSelectedPlayers());
					gameRunner.resumeGame();
				} else {
					gameRunner.pauseGame(false);
				}
			}, newGameButton, backButton, this));
		}

		public void gameStarted() {
			paused.set(false);
			setText("Pause");
		}

		public void gameEnded() {
			paused.set(true);
			setText("  Play  ");
		}
	}
}
