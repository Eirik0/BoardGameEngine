package main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import analysis.ComputerPlayer;
import analysis.ComputerPlayerInfo;
import game.IPosition;
import gui.GameRegistry;
import main.PlayerSelectionPanel.PlayerSelectionItem;

public class ComputerConfigurationDialog<M, P extends IPosition<M, P>> {
	private final JFrame configurationFrame;
	private final JPanel configurationPanel;
	private final ComputerPlayerInfo<M, P> computerPlayerInfo;

	public ComputerConfigurationDialog(JComboBox<PlayerSelectionPanel.PlayerSelectionItem> playerComboBox, String gameName, ComputerPlayerInfo<M, P> computerPlayerInfo, boolean infiniteTimeOnly) {
		this.computerPlayerInfo = computerPlayerInfo;

		String[] strategyNames = GameRegistry.getStrategyNames(gameName).toArray(new String[0]);
		JComboBox<String> strategyComboBox = BoardGameEngineMain.initComponent(new JComboBox<>(strategyNames));

		JTextField numWorkersField = BoardGameEngineMain.initComponent(new JTextField(Integer.toString(computerPlayerInfo.numWorkers), 3));
		numWorkersField.setFocusable(true);

		JTextField msPerMoveField;
		if (infiniteTimeOnly) {
			msPerMoveField = BoardGameEngineMain.initComponent(new JTextField("Inf", 5));
		} else {
			msPerMoveField = BoardGameEngineMain.initComponent(new JTextField(Long.toString(computerPlayerInfo.msPerMove), 10));
			msPerMoveField.setFocusable(true);
		}

		JPanel centerPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		centerPanel.add(strategyComboBox);
		centerPanel.add(Box.createHorizontalStrut(10));
		centerPanel.add(BoardGameEngineMain.initComponent(new JLabel("Threads:")));
		centerPanel.add(numWorkersField);
		centerPanel.add(Box.createHorizontalStrut(10));
		centerPanel.add(BoardGameEngineMain.initComponent(new JLabel("ms per move:")));
		centerPanel.add(msPerMoveField);

		JButton okButton = BoardGameEngineMain.initComponent(new JButton("Ok"));
		okButton.addActionListener(e -> {
			int numWorkers = computerPlayerInfo.numWorkers;
			try {
				numWorkers = Integer.parseInt(numWorkersField.getText());
				numWorkers = Math.min(Math.max(1, numWorkers), computerPlayerInfo.maxWorkers);
			} catch (Exception ex) {
				numWorkersField.setText(Integer.toString(computerPlayerInfo.numWorkers));
			}
			long msPerMove = computerPlayerInfo.msPerMove;
			if (!infiniteTimeOnly) {
				try {
					msPerMove = Integer.parseInt(msPerMoveField.getText());
					msPerMove = Math.max(100, msPerMove);
				} catch (Exception ex) {
					msPerMoveField.setText(Long.toString(computerPlayerInfo.msPerMove));
				}
			}
			String strategyName = strategyComboBox.getItemAt(strategyComboBox.getSelectedIndex());
			GameRegistry.updateComputerPlayerInfo(computerPlayerInfo, gameName, strategyName, numWorkers, msPerMove);
			playerComboBox.repaint();
			playerComboBox.setPrototypeDisplayValue(new PlayerSelectionItem(ComputerPlayer.NAME, computerPlayerInfo));
			hide();
		});

		JButton cancelButton = BoardGameEngineMain.initComponent(new JButton("Cancel"));
		cancelButton.addActionListener(e -> hide());

		JPanel bottomPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		bottomPanel.add(okButton);
		bottomPanel.add(cancelButton);

		configurationPanel = new JPanel(new BorderLayout());
		configurationPanel.add(centerPanel, BorderLayout.CENTER);
		configurationPanel.add(bottomPanel, BorderLayout.SOUTH);

		configurationFrame = new JFrame("Configure");
		configurationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		configurationFrame.setFocusable(false);
		configurationFrame.setContentPane(configurationPanel);
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
			configurationPanel.requestFocus();
		});
	}

	public ComputerPlayerInfo<?, ?> getComputerPlayerInfo() {
		return computerPlayerInfo;
	}
}
