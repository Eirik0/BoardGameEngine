package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import game.GameRunner;
import game.IGame;
import game.IPlayer;
import gui.GameGuiManager;
import gui.MainMenuState;

@SuppressWarnings("serial")
public class PlayerControllerPanel extends JPanel {
	private final List<JComboBox<IPlayer>> playerComboBoxes = new ArrayList<>();
	private Runnable backAction;

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		int numberOfPlayers = game.getNumberOfPlayers();
		IPlayer[] avaliablePlayers = game.getAvailablePlayers();
		IPlayer defaultPlayer = game.getDefaultPlayer();
		playerComboBoxes.clear();
		for (int i = 0; i < numberOfPlayers; i++) {
			playerComboBoxes.add(createPlayerComboBox(avaliablePlayers, defaultPlayer));
		}
		rebuildWith(game, gameRunner);
	}

	public void setBackAction(Runnable backAction) {
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
		newGameButton.setFocusable(false);
		endGameButton.setFocusable(false);

		newGameButton.addActionListener(createEnableDisableRunnableWrapper(newGameButton, endGameButton, () -> startNewGame(game, gameRunner)));
		endGameButton.addActionListener(createEnableDisableRunnableWrapper(newGameButton, endGameButton, () -> gameRunner.endGame()));

		buttonPanel.add(Box.createHorizontalStrut(30));
		buttonPanel.add(newGameButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(endGameButton);

		JButton backButton = new JButton("Back");
		backButton.addActionListener(e -> {
			gameRunner.endGame();
			backAction.run();
			GameGuiManager.setGameState(new MainMenuState());
		});

		add(new JLabel(game.getName()), BorderLayout.WEST);
		add(buttonPanel, BorderLayout.CENTER);
		add(backButton, BorderLayout.EAST);
	}

	private ActionListener createEnableDisableRunnableWrapper(JButton newGameButton, JButton endGameButton, Runnable r) {
		return e -> {
			try {
				newGameButton.setEnabled(false);
				endGameButton.setEnabled(false);
				r.run();
			} finally {
				endGameButton.setEnabled(true);
				newGameButton.setEnabled(true);
			}
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
