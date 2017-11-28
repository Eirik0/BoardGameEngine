package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import game.IPosition;
import main.PlayerSelectionPanel.PlayerSelectionItem;

public class ComputerConfigurationDialog<M, P extends IPosition<M, P>> {
	private final ComputerConfigurationPanel<M, P> configurationPanel;

	private final JFrame configurationFrame;
	private final JPanel configurationDialogPanel;

	public ComputerConfigurationDialog(JComboBox<PlayerSelectionPanel.PlayerSelectionItem> playerComboBox, String gameName, ComputerPlayerInfo<M, P> computerPlayerInfo, boolean infiniteTimeOnly) {
		configurationPanel = new ComputerConfigurationPanel<>(gameName, computerPlayerInfo, infiniteTimeOnly);

		JButton okButton = BoardGameEngineMain.initComponent(new JButton("Ok"));
		okButton.addActionListener(e -> {
			configurationPanel.updateComputerPlayerInfo();
			playerComboBox.repaint();
			playerComboBox.setPrototypeDisplayValue(new PlayerSelectionItem(ComputerPlayer.NAME, computerPlayerInfo));
			hide();
		});

		JButton cancelButton = BoardGameEngineMain.initComponent(new JButton("Cancel"));
		cancelButton.addActionListener(e -> hide());

		JPanel bottomPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.TRAILING)));
		bottomPanel.add(okButton);
		bottomPanel.add(cancelButton);

		configurationDialogPanel = new JPanel(new BorderLayout());
		configurationDialogPanel.add(configurationPanel, BorderLayout.CENTER);
		configurationDialogPanel.add(bottomPanel, BorderLayout.SOUTH);

		configurationFrame = new JFrame("Configure");
		configurationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		configurationFrame.setFocusable(false);
		configurationFrame.setContentPane(configurationDialogPanel);
	}

	public void hide() {
		SwingUtilities.invokeLater(() -> {
			configurationFrame.setVisible(false);
		});
	}

	public void show() {
		SwingUtilities.invokeLater(() -> {
			configurationFrame.pack();
			configurationFrame.setLocationRelativeTo(null);
			configurationFrame.setVisible(true);
			configurationDialogPanel.requestFocus();
		});
	}

	public ComputerPlayerInfo<M, P> getComputerPlayerInfo() {
		return configurationPanel.getComputerPlayerInfo();
	}
}
