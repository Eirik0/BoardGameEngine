package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import game.GameRunner;
import game.IGame;
import game.IPlayer;

@SuppressWarnings("serial")
public class PlayerControllerPanel extends JPanel {
	private final List<JComboBox<IPlayer>> playerComboBoxes = new ArrayList<>();

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new FlowLayout(FlowLayout.CENTER));
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

	private JComboBox<IPlayer> createPlayerComboBox(IPlayer[] availablePlayers, IPlayer defaultPlayer) {
		JComboBox<IPlayer> jComboBox = new JComboBox<>(availablePlayers);
		jComboBox.setSelectedItem(defaultPlayer);
		return jComboBox;
	}

	private void rebuildWith(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		addWithPadding(new JLabel(game.getName()), 20);
		for (int i = 0; i < playerComboBoxes.size(); i++) {
			addWithPadding(new JLabel("Player " + i + ": "), 20);
			add(playerComboBoxes.get(i));
		}

		JButton newGameButton = new JButton("New Game");
		JButton endGameButton = new JButton("End Game");
		newGameButton.setFocusable(false);
		endGameButton.setFocusable(false);

		newGameButton.addActionListener(createEnableDisableRunnableWrapper(newGameButton, endGameButton, () -> startNewGame(game, gameRunner)));
		newGameButton.addActionListener(createEnableDisableRunnableWrapper(newGameButton, endGameButton, () -> endGame(gameRunner)));

		addWithPadding(newGameButton, 30);
		addWithPadding(endGameButton, 20);
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

	private void addWithPadding(JComponent component, int padding) {
		add(Box.createHorizontalStrut(padding));
		add(component);
	}

	private void startNewGame(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		List<IPlayer> players = new ArrayList<>();
		for (JComboBox<IPlayer> jComboBox : playerComboBoxes) {
			players.add(jComboBox.getItemAt(jComboBox.getSelectedIndex()));
		}
		gameRunner.startNewGame(players);
	}

	private void endGame(GameRunner<?, ?> gameRunner) {
		gameRunner.endGame();
	}
}
