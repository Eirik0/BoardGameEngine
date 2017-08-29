package main;

import java.awt.BorderLayout;
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

import game.GameRunner;
import game.IGame;
import game.IPlayer;
import gui.GameGuiManager;
import gui.gamestate.MainMenuState;

@SuppressWarnings("serial")
public class PlayerControllerPanel extends JPanel {
	private final JLabel gameLabel = BoardGameEngineMain.initComponent(new JLabel());
	private final List<JComboBox<IPlayer>> playerComboBoxes;
	private Runnable backAction;

	public PlayerControllerPanel(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);
		gameLabel.setText(game.getName());
		int numberOfPlayers = game.getNumberOfPlayers();
		IPlayer[] avaliablePlayers = game.getAvailablePlayers();
		IPlayer defaultPlayer = game.getDefaultPlayer();
		playerComboBoxes = new ArrayList<>();
		for (int i = 0; i < numberOfPlayers; i++) {
			playerComboBoxes.add(createPlayerComboBox(avaliablePlayers, defaultPlayer));
		}
		rebuildWith(game, gameRunner);
		gameRunner.setEndGameAction(() -> gameLabel.setText(game.getName()));
	}

	public void setBackAction(Runnable backAction) { // this allows for easy self reference outside of this
		this.backAction = backAction;
	}

	private JComboBox<IPlayer> createPlayerComboBox(IPlayer[] availablePlayers, IPlayer defaultPlayer) {
		JComboBox<IPlayer> jComboBox = BoardGameEngineMain.initComponent(new JComboBox<>(availablePlayers));
		jComboBox.setSelectedItem(defaultPlayer);
		return jComboBox;
	}

	private void rebuildWith(IGame<?, ?> game, GameRunner<?, ?> gameRunner) {
		JPanel buttonPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.CENTER)));
		buttonPanel.add(Box.createHorizontalStrut(30));
		for (int i = 0; i < playerComboBoxes.size(); i++) {
			buttonPanel.add(playerComboBoxes.get(i));
			if (i < playerComboBoxes.size() - 1) {
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
		for (JComboBox<IPlayer> jComboBox : playerComboBoxes) {
			players.add(jComboBox.getItemAt(jComboBox.getSelectedIndex()));
		}
		gameRunner.startNewGame(players);
		gameLabel.setText(game.getName() + "...");
	}
}
