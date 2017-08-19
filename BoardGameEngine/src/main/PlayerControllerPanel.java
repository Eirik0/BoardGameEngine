package main;

import game.GameRunner;
import game.IGame;
import game.IPlayer;
import gui.GameGuiManager;
import gui.gamestate.MainMenuState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PlayerControllerPanel extends JPanel {
	private final List<JComboBox<IPlayer>> playerComboBoxes;
	private Runnable backAction;

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		int numberOfPlayers = game.getNumberOfPlayers();
		IPlayer[] avaliablePlayers = game.getAvailablePlayers();
		IPlayer defaultPlayer = game.getDefaultPlayer();
		playerComboBoxes = new ArrayList<>();
		for (int i = 0; i < numberOfPlayers; i++) {
			playerComboBoxes.add(createPlayerComboBox(avaliablePlayers, defaultPlayer));
		}
		rebuildWith(game, gameRunner);
	}

	public void setBackAction(Runnable backAction) { // this allows for easy self reference outside of this
		this.backAction = backAction;
	}

	private JComboBox<IPlayer> createPlayerComboBox(IPlayer[] availablePlayers, IPlayer defaultPlayer) {
		JComboBox<IPlayer> jComboBox = new JComboBox<>(availablePlayers);
		jComboBox.setSelectedItem(defaultPlayer);
		jComboBox.setFocusable(false);
		return jComboBox;
	}

	private void rebuildWith(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(Box.createHorizontalStrut(30));
		for (int i = 0; i < playerComboBoxes.size(); i++) {
			buttonPanel.add(playerComboBoxes.get(i));
			if (i < playerComboBoxes.size() - 1) {
				buttonPanel.add(new JLabel(" v. "));
			}
		}

		JButton newGameButton = new JButton("New Game");
		JButton endGameButton = new JButton("End Game");
		JButton backButton = new JButton("Back");
		newGameButton.setFocusable(false);
		endGameButton.setFocusable(false);
		backButton.setFocusable(false);
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

		add(new JLabel(game.getName()), BorderLayout.WEST);
		add(buttonPanel, BorderLayout.CENTER);
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
			}).start();
		};
	}

	private void startNewGame(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		List<IPlayer> players = new ArrayList<>();
		for (JComboBox<IPlayer> jComboBox : playerComboBoxes) {
			players.add(jComboBox.getItemAt(jComboBox.getSelectedIndex()));
		}
		gameRunner.startNewGame(players);
	}
}
