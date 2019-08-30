package bge.gui.movehistory;

import bge.igame.MoveHistory;
import gt.component.IMouseTracker;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.location.EGluedLocation;
import gt.ecomponent.location.GlueSide;
import gt.ecomponent.location.SizedComponentLocationAdapter;
import gt.ecomponent.scrollbar.EScrollPane;
import gt.gameentity.IGameImageDrawer;
import gt.gameentity.IGraphics;
import gt.gameentity.Sized;
import gt.gamestate.GameState;
import gt.gamestate.UserInput;

public class MoveHistoryState<M> implements GameState, Sized {
    public static final int TITLE_HEIGHT = 25;

    double width;
    double height;

    private final EComponentPanel componentPanel;
    private final EScrollPane scrollPane;

    public MoveHistoryState(MoveHistory<M> guiMoveHistory, IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        SizedComponentLocationAdapter cl = new SizedComponentLocationAdapter(this, 0, 0);
        EGluedLocation spLoc = cl.createPaddedLocation(0, TITLE_HEIGHT, 0, 0);
        scrollPane = new EScrollPane(spLoc, new MoveHistoryViewport<>(guiMoveHistory, spLoc), imageDrawer);
        componentPanel = new EComponentPanelBuilder(mouseTracker)
                .add(0, new ETextLabel(cl.createGluedLocation(GlueSide.TOP, 0, 0, 0, TITLE_HEIGHT - 1), "Move History", true))
                .add(0, scrollPane)
                .build();
    }

    @Override
    public void update(double dt) {
        componentPanel.update(dt);
        // TODO: only resize when a move is added
        scrollPane.setSize(width, height - TITLE_HEIGHT);
    }

    @Override
    public void drawOn(IGraphics graphics) {
        componentPanel.drawOn(graphics);
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        scrollPane.setSize(width, height - TITLE_HEIGHT);
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
        componentPanel.handleUserInput(input);
    }
}
