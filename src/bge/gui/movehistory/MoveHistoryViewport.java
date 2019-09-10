package bge.gui.movehistory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import bge.gui.gamestate.BoardGameState;
import bge.igame.MoveHistory.HistoryMove;
import bge.igame.MoveHistory.MoveIndex;
import gt.component.ComponentCreator;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.scrollbar.EScrollBar;
import gt.ecomponent.scrollbar.EViewport;
import gt.ecomponent.scrollbar.ViewportWindow;
import gt.gameentity.IGraphics;

public class MoveHistoryViewport<M> implements EViewport {
    public static final int ITEM_HEIGHT = 25;

    private final BoardGameState<M> parent;
    private final EComponentLocation cl;
    private final ViewportWindow window;

    private final List<GuiHistoryMove> guiMoveHistory = new ArrayList<>();
    private int numMoveRows = 0;
    private int numMoveCols = 0;

    private GuiHistoryMove mouseOverMove = null;
    private GuiHistoryMove selectedMove = null;

    public MoveHistoryViewport(BoardGameState<M> parent, EComponentLocation cl) {
        this.parent = parent;
        this.cl = cl;
        window = new ViewportWindow(this, 0, 0, cl.getWidth(), cl.getHeight(), 0, ITEM_HEIGHT);
    }

    public boolean setMoveHistoryList(List<HistoryMove<M>> moveHistoryList, MoveIndex historyMoveIndex) {
        selectedMove = null;
        guiMoveHistory.clear();
        for (int i = 0; i < moveHistoryList.size(); ++i) {
            M[] historyMoves = moveHistoryList.get(i).moves;
            numMoveCols = Math.max(numMoveCols, historyMoves.length);
            for (int j = 0; j < historyMoves.length; ++j) {
                if (historyMoves[j] != null) {
                    GuiHistoryMove move = new GuiHistoryMove(i, j, historyMoves[j].toString());
                    guiMoveHistory.add(move);
                    if (historyMoveIndex.moveNumber == i && historyMoveIndex.playerNum == j) {
                        selectedMove = move;
                    }
                }
            }
        }
        numMoveRows = moveHistoryList.size();
        return selectedMove != null && selectedMove.moveRow == numMoveRows -1;
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
        for (int i = 0; i < numMoveRows; ++i) {
            double y = i * ITEM_HEIGHT - window.getTruncatedY0(ITEM_HEIGHT);
            g.drawCenteredYString((i + 1) + ". ", 5, y + ITEM_HEIGHT / 2);
        }
        for (GuiHistoryMove move : guiMoveHistory) {
            g.drawCenteredYString(move.moveString, move.getX0() + 5, move.getY0() + ITEM_HEIGHT / 2);
        }
        if (mouseOverMove != null) {
            g.drawRect(mouseOverMove.getX0(), mouseOverMove.getY0(), moveWidth(), ITEM_HEIGHT, Color.GREEN);
        }
        if (selectedMove != null) {
            double x0 = selectedMove.getX0();
            double y0 = selectedMove.getY0() + ITEM_HEIGHT - 2;
            g.setColor(Color.CYAN);
            g.drawLine(x0, y0, x0 + moveWidth() - 1, y0);
        }
    }

    private double moveWidth() {
        return getWidth() / (numMoveCols + 2);
    }

    @Override
    public double getWidth() {
        boolean scrollBar = (numMoveRows * ITEM_HEIGHT) > cl.getHeight();
        return scrollBar ? cl.getWidth() - EScrollBar.BAR_WIDTH : cl.getWidth();
    }

    @Override
    public double getHeight() {
        return Math.max(cl.getHeight(), numMoveRows * ITEM_HEIGHT - 1);
    }

    @Override
    public boolean setMouseOver(double screenX, double screenY) {
        boolean containsPoint = window.containsPoint(screenX, screenY);
        if (containsPoint) {
            for (GuiHistoryMove guiHistoryMove : guiMoveHistory) {
                if (guiHistoryMove.containsPoint(screenX, screenY)) {
                    mouseOverMove = guiHistoryMove;
                    return true;
                }
            }
        }
        mouseOverMove = null;
        return containsPoint;
    }

    @Override
    public boolean setMousePressed(double screenX, double screenY) {
        return window.containsPoint(screenX, screenY);
    }

    @Override
    public void setMouseReleased(double screenX, double screenY) {
        boolean containsPoint = window.containsPoint(screenX, screenY);
        if (containsPoint) {
            for (GuiHistoryMove guiHistoryMove : guiMoveHistory) {
                if (guiHistoryMove.containsPoint(screenX, screenY)) {
                    parent.setMoveFromHistory(guiHistoryMove.moveRow, guiHistoryMove.moveCol);
                    return;
                }
            }
        }
    }

    @Override
    public void focusLost(boolean fromClick) {
        mouseOverMove = null;
    }

    private class GuiHistoryMove {
        public final int moveRow;
        public final int moveCol;
        public final String moveString;

        public GuiHistoryMove(int moveRow, int moveCol, String moveString) {
            this.moveRow = moveRow;
            this.moveCol = moveCol;
            this.moveString = moveString;
        }

        public double getX0() {
            return (moveCol + 1) * moveWidth() - 5;
        }

        public double getY0() {
            return moveRow * ITEM_HEIGHT - window.getTruncatedY0(ITEM_HEIGHT);
        }

        public boolean containsPoint(double screenX, double screenY) {
            double x0 = getX0();
            double y0 = getY0();
            return screenX >= x0 && screenX < (x0 + moveWidth()) && screenY >= y0 && screenY < (y0 + ITEM_HEIGHT);
        }
    }
}
