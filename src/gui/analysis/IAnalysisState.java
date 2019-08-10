package gui.analysis;

import javax.swing.JPanel;

import game.IPosition;
import gui.Sizable;
import gui.gamestate.GameState;

public interface IAnalysisState<M> extends Sizable, GameState {
    public void setPosition(IPosition<M> position);

    public void stopAnalysis();

    public JPanel getTopPanel();

    public void setOnResize(Runnable onResize);

    @Override
    public default void componentResized(int width, int height) {
        checkResized(width, height);
    }
}
