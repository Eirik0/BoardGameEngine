package bge.gui.analysis;

import javax.swing.JPanel;

import bge.igame.IPosition;
import gt.gameentity.Sized;
import gt.gamestate.GameState;

public interface IAnalysisState<M> extends GameState, Sized {
    void setPosition(IPosition<M> position);

    void stopAnalysis();

    JPanel getTopPanel();

    void setOnResize(Runnable onResize);
}
