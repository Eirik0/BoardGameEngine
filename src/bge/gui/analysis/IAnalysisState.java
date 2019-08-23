package bge.gui.analysis;

import javax.swing.JPanel;

import bge.game.IPosition;
import bge.gui.gamestate.GameState;
import gt.gameentity.SizedSizable;

public interface IAnalysisState<M> extends SizedSizable, GameState {
    void setPosition(IPosition<M> position);

    void stopAnalysis();

    JPanel getTopPanel();

    void setOnResize(Runnable onResize);

    @Override
    default void componentResized(int width, int height) {
        setSize(width, height);
    }
}
