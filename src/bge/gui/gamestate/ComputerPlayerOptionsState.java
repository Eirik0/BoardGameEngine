package bge.gui.gamestate;

import bge.igame.player.PlayerOptions;
import gt.gameentity.GameEntity;
import gt.gameentity.IGraphics;
import gt.gameentity.Sized;
import gt.gameentity.UserInputHandler;
import gt.gamestate.UserInput;

public class ComputerPlayerOptionsState implements GameEntity, UserInputHandler, Sized {
    double width;// Constants?
    double height;

    private final PlayerOptions options;

    public ComputerPlayerOptionsState(PlayerOptions options) {
        this.options = options;
    }

    @Override
    public void update(double dt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawOn(IGraphics g) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void handleUserInput(UserInput input) {
        // TODO Auto-generated method stub

    }
}
