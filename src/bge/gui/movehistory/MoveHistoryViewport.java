package bge.gui.movehistory;

import java.util.ArrayList;
import java.util.List;

import bge.igame.MoveHistory.HistoryMove;
import gt.component.ComponentCreator;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.scrollbar.EScrollBar;
import gt.ecomponent.scrollbar.EViewport;
import gt.ecomponent.scrollbar.ViewportWindow;
import gt.gameentity.IGraphics;

public class MoveHistoryViewport<M> implements EViewport {
    public static final int ITEM_HEIGHT = 25;

    private final EComponentLocation cl;
    private final ViewportWindow window;

    private List<HistoryMove<M>> moveHistoryList;

    public MoveHistoryViewport(EComponentLocation cl) {
        this.cl = cl;
        window = new ViewportWindow(this, 0, 0, cl.getWidth(), cl.getHeight(), 0, ITEM_HEIGHT);
        moveHistoryList = new ArrayList<>();
    }

    public void setMoveHistoryList(List<HistoryMove<M>> moveHistoryList) {
        this.moveHistoryList = moveHistoryList;
    }

    @Override
    public ViewportWindow getWindow() {
        return window;
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void drawOn(IGraphics g) {
        g.fillRect(0, 0, window.getWidth(), window.getHeight(), ComponentCreator.backgroundColor());
        g.setColor(ComponentCreator.foregroundColor());
        for (int i = 0; i < moveHistoryList.size(); ++i) {
            HistoryMove<M> historyMove = moveHistoryList.get(i);
            M[] moves = historyMove.moves;
            double y = i * ITEM_HEIGHT - window.getTruncatedY0(ITEM_HEIGHT);
            g.drawCenteredYString((i + 1) + ". ", 5, y + ITEM_HEIGHT / 2);
            for (int j = 0; j < moves.length; ++j) {
                String moveStr = moves[j] == null ? "" : moves[j].toString();
                g.drawCenteredYString(moveStr, 5 + (j + 1) * 50, y + ITEM_HEIGHT / 2);
            }
        }
    }

    @Override
    public double getWidth() {
        boolean scrollBar = (moveHistoryList.size() * ITEM_HEIGHT) > cl.getHeight();
        return scrollBar ? cl.getWidth() - EScrollBar.BAR_WIDTH : cl.getWidth();
    }

    @Override
    public double getHeight() {
        return Math.max(cl.getHeight(), moveHistoryList.size() * ITEM_HEIGHT - 1);
    }

    @Override
    public boolean setMouseOver(double screenX, double screenY) {
        return window.containsPoint(screenX, screenY);
    }

    @Override
    public boolean setMousePressed(double screenX, double screenY) {
        return window.containsPoint(screenX, screenY);
    }

    @Override
    public void setMouseReleased(double screenX, double screenY) {
    }

    @Override
    public void focusLost(boolean fromClick) {
    }
}
