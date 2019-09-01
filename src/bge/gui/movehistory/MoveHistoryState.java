package bge.gui.movehistory;

import java.awt.Color;
import java.util.List;

import bge.igame.MoveHistory.HistoryMove;
import gt.component.IMouseTracker;
import gt.ecomponent.EComponentPanel;
import gt.ecomponent.EComponentPanelBuilder;
import gt.ecomponent.ETextLabel;
import gt.ecomponent.list.EComponentLocation;
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
    private final EComponentLocation spLoc;
    private final EScrollPane scrollPane;

    private final MoveHistoryViewport<M> view;

    public MoveHistoryState(IMouseTracker mouseTracker, IGameImageDrawer imageDrawer) {
        SizedComponentLocationAdapter cl = new SizedComponentLocationAdapter(this, 0, 0);
        spLoc = cl.createPaddedLocation(1, TITLE_HEIGHT, 1, 1);
        view = new MoveHistoryViewport<>(spLoc);
        scrollPane = new EScrollPane(spLoc, view, imageDrawer);
        componentPanel = new EComponentPanelBuilder(mouseTracker)
                .add(0, new ETextLabel(cl.createGluedLocation(GlueSide.TOP, 0, 0, 0, TITLE_HEIGHT - 1), "Move History", true))
                .add(0, scrollPane)
                .build();
    }

    public void setMoveHistoryList(List<HistoryMove<M>> moveHistoryList) {
        view.setMoveHistoryList(moveHistoryList);
        scrollPane.setSize(spLoc.getWidth(), spLoc.getHeight());
        view.getWindow().move(0, MoveHistoryViewport.ITEM_HEIGHT);
    }

    @Override
    public void update(double dt) {
        componentPanel.update(dt);
    }

    @Override
    public void drawOn(IGraphics graphics) {
        componentPanel.drawOn(graphics);
        graphics.drawRect(0, 0, width, height, Color.RED);
    }

    @Override
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        scrollPane.setSize(spLoc.getWidth(), spLoc.getHeight());
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
