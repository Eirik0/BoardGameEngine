package gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import analysis.ComputerPlayer;
import game.IPosition;
import main.BoardGameEngineMain;

public class ComputerPlayerObservationState<M, P extends IPosition<M, P>> implements IAnalysisState<M, P> {
	private int width;
	private int height;

	private final JPanel titlePanel;
	private final ComputerPlayerObserver observer;

	public ComputerPlayerObservationState(ComputerPlayer computerPlayer, int playerNum) {
		JLabel nameLabel = BoardGameEngineMain.initComponent(new JLabel(computerPlayer.toString()));
		JLabel depthLabel = BoardGameEngineMain.initComponent(new JLabel(String.format("depth = %-3d", 0)));

		observer = new ComputerPlayerObserver(computerPlayer, playerNum, name -> nameLabel.setText(name), depth -> depthLabel.setText(depth));

		titlePanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));

		JPanel titleLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		titleLabelPanel.add(nameLabel);

		JPanel depthLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.TRAILING)));
		depthLabelPanel.add(depthLabel);

		titlePanel.add(titleLabelPanel, BorderLayout.WEST);
		titlePanel.add(depthLabelPanel, BorderLayout.EAST);
	}

	@Override
	public void componentResized(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void handleUserInput(UserInput input) {
		// Do nothing
	}

	@Override
	public void drawOn(Graphics2D graphics) {
		fillRect(graphics, 0, 0, width, height, BoardGameEngineMain.BACKGROUND_COLOR);
		observer.drawOn(graphics, width, height);
	}

	@Override
	public void setPosition(P position) {
		// do nothing
	}

	@Override
	public synchronized void stopAnalysis() {
		observer.stopObserving();
	}

	@Override
	public JPanel getTopPanel() {
		return titlePanel;
	}
}
