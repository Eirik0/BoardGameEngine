package main;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import game.IPlayer;
import gui.GameRegistry;

@SuppressWarnings("serial")
public class PlayerSelectionPanel extends JPanel {
	private final JComboBox<PlayerSelectionItem> comboBox;
	private final ComputerConfigurationDialog computerConfigurationDialog;
	private final JButton configureButton;

	public PlayerSelectionPanel(String[] availablePlayers, String gameName) {
		setLayout(new BorderLayout());
		BoardGameEngineMain.initComponent(this);

		ComputerPlayerInfo<?, ?> computerPlayerInfo = GameRegistry.newDefaultComputerPlayerInfo(gameName);

		configureButton = BoardGameEngineMain.initComponent(new JButton("C"));
		configureButton.setMargin(new Insets(0, 5, 0, 5));

		comboBox = createPlayerComboBox(availablePlayers, computerPlayerInfo);

		computerConfigurationDialog = new ComputerConfigurationDialog(comboBox, gameName, computerPlayerInfo, computerPlayerInfo.infiniteTimeOnly);

		configureButton.addActionListener(e -> computerConfigurationDialog.show());

		add(comboBox, BorderLayout.CENTER);
	}

	private JComboBox<PlayerSelectionItem> createPlayerComboBox(String[] availablePlayers, ComputerPlayerInfo<?, ?> computerPlayerInfo) {
		PlayerSelectionItem[] playerSelectionItems = new PlayerSelectionItem[availablePlayers.length];
		for (int i = 0; i < availablePlayers.length; ++i) {
			playerSelectionItems[i] = new PlayerSelectionItem(availablePlayers[i], computerPlayerInfo);
		}
		JComboBox<PlayerSelectionItem> jComboBox = BoardGameEngineMain.initComponent(new JComboBox<>(playerSelectionItems));
		jComboBox.addActionListener(e -> {
			PlayerSelectionItem playerSelectionItem = jComboBox.getItemAt(jComboBox.getSelectedIndex());
			if (ComputerPlayer.NAME.equals(playerSelectionItem.playerName)) {
				add(configureButton, BorderLayout.EAST);
			} else {
				remove(configureButton);
			}
			revalidate();
		});

		jComboBox.setSelectedItem(playerSelectionItems[0]);
		return jComboBox;
	}

	public IPlayer getPlayer(String gameName) {
		return GameRegistry.getPlayer(gameName, comboBox.getItemAt(comboBox.getSelectedIndex()).playerName, computerConfigurationDialog.getComputerPlayerInfo());
	}

	static class PlayerSelectionItem {
		final String playerName;
		private final ComputerPlayerInfo<?, ?> computerPlayerInfo;

		public PlayerSelectionItem(String playerName, ComputerPlayerInfo<?, ?> computerPlayerInfo) {
			this.playerName = playerName;
			this.computerPlayerInfo = computerPlayerInfo;
		}

		@Override
		public String toString() {
			if (ComputerPlayer.NAME.equals(playerName)) {
				return computerPlayerInfo.toString();
			} else {
				return playerName;
			}
		}

		@Override
		public int hashCode() {
			return playerName.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			PlayerSelectionItem other = (PlayerSelectionItem) obj;
			return playerName.equals(other.playerName);
		}
	}
}
