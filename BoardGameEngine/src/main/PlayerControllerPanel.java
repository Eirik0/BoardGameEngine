package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private final JLabel gameLabel = BoardGameEngineMain.initComponent(new JLabel());
	private final List<PlayerSelectionPanel> playerSelectionPanels;
	private Runnable backAction;

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);
		gameLabel.setText(game.getName());
		int numberOfPlayers = game.getNumberOfPlayers();
		String[] playerNames = GameRegistry.getPlayerNames(game.getName()).toArray(new String[0]);
		String[] availablePlayers = playerNames;
		playerSelectionPanels = new ArrayList<>();
		for (int i = 0; i < numberOfPlayers; i++) {
			playerSelectionPanels.add(new PlayerSelectionPanel(availablePlayers, game.getName()));
		}
		rebuildWith(game, gameRunner);
		gameRunner.setGameOverAction(() -> gameLabel.setText(game.getName()));
	}

	public void setBackAction(Runnable backAction) { // this allows for easy self reference outside of this
		this.backAction = backAction;
	}

	private void rebuildWith(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
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
		JButton endGameButton = BoardGameEngineMain.initComponent(new JButton("End Game"));
		JButton backButton = BoardGameEngineMain.initComponent(new JButton("Back"));
		List<JButton> buttons = Arrays.asList(newGameButton, endGameButton, backButton);

		newGameButton.addActionListener(createEnableDisableRunnableWrapper(buttons, () -> startNewGame(game, gameRunner)));
		endGameButton.addActionListener(createEnableDisableRunnableWrapper(buttons, () -> gameRunner.endGame()));
		backButton.addActionListener(createEnableDisableRunnableWrapper(buttons, () -> {
			gameRunner.endGame();
			backAction.run();
			GameGuiManager.setGameState(new MainMenuState());
		}));

		buttonPanel.add(Box.createHorizontalStrut(30));
		buttonPanel.add(newGameButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(endGameButton);

		add(gameLabel, BorderLayout.WEST);
		add(buttonPanelWrapper, BorderLayout.CENTER);
		add(backButton, BorderLayout.EAST);
	}

	private ActionListener createEnableDisableRunnableWrapper(List<JButton> buttons, Runnable r) {
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

	private void startNewGame(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		List<IPlayer> players = new ArrayList<>();
		for (PlayerSelectionPanel playerSelectionPanel : playerSelectionPanels) {
			players.add(playerSelectionPanel.getPlayer(game.getName()));
		}
		gameRunner.startNewGame(players);
		gameLabel.setText(game.getName() + "...");
	}
}
