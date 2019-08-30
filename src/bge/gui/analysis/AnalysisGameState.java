package bge.gui.analysis;

import gt.component.ComponentCreator;
import gt.gameentity.IGraphics;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class AnalysisGameState implements GameState {
    double width;
    double height;

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(IGraphics g) {
        g.fillRect(0, 0, width, height, ComponentCreator.backgroundColor());
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void handleUserInput(UserInput input) {
    }
}
