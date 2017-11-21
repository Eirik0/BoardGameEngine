package gui.analysis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

import main.BoardGameEngineMain;

public class InfiniteAnalysisState implements IAnalysisState {
	private int width;
	private int height;

	private final JPanel optionsPanel;

	public InfiniteAnalysisState(int playerNum) {
		optionsPanel = BoardGameEngineMain.initComponent(new JPanel(new BorderLayout()));
		JPanel titleLabelPanel = BoardGameEngineMain.initComponent(new JPanel(new FlowLayout(FlowLayout.LEADING)));
		titleLabelPanel.add(BoardGameEngineMain.initComponent(new JLabel("Infinite Analysis")));
		optionsPanel.add(titleLabelPanel, BorderLayout.WEST);
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
		graphics.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
	}

	@Override
	public void stopAnalysis() {
		// TODO Auto-generated method stub
	}

	@Override
	public JPanel getTopPanel() {
		return optionsPanel;
	}
}
