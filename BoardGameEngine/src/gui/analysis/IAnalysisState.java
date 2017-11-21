package gui.analysis;

import javax.swing.JPanel;

import gui.gamestate.GameState;

public interface IAnalysisState extends GameState {
	public void stopAnalysis();

	public JPanel getTopPanel();
}
