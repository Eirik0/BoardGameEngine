package gui.analysis;

import javax.swing.JPanel;

import game.IPosition;
import gui.gamestate.GameState;

public interface IAnalysisState<M, P extends IPosition<M, P>> extends GameState {
	public void setPosition(P position);

	public void stopAnalysis();

	public JPanel getTopPanel();
}
