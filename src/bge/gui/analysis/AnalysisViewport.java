package bge.gui.analysis;

import java.util.ArrayList;
import java.util.List;

import bge.analysis.AnalysisResult;
import gt.component.ComponentCreator;
import gt.ecomponent.list.EComponentLocation;
import gt.ecomponent.scrollbar.EScrollBar;
import gt.ecomponent.scrollbar.EViewport;
import gt.ecomponent.scrollbar.ViewportWindow;
import gt.gameentity.IGraphics;

public class AnalysisViewport implements EViewport {
    public static final int ITEM_HEIGHT = 25;

    private final EComponentLocation cl;
    private final ViewportWindow window;

    private List<ObservedMoveWithScore> analyzedMoves = new ArrayList<>();

    public AnalysisViewport(EComponentLocation cl) {
        this.cl = cl;
        window = new ViewportWindow(this, 0, 0, cl.getWidth(), cl.getHeight(), 0, ITEM_HEIGHT);
    }

    public void setAnalyzedMoves(List<ObservedMoveWithScore> analyzedMoves) {
        this.analyzedMoves = analyzedMoves;
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
        for (int i = 0; i < analyzedMoves.size(); ++i) {
            ObservedMoveWithScore moveWithScore = analyzedMoves.get(i);
            g.setColor(moveWithScore.isPartial ? ComponentCreator.foregroundColor() : ComponentCreator.foregroundColor().darker());
            double y = i * ITEM_HEIGHT - window.getTruncatedY0(ITEM_HEIGHT);
            g.drawCenteredYString((i + 1) + ". " + getScoreString(moveWithScore.score), 5, y + ITEM_HEIGHT / 2);
            g.drawCenteredYString(moveWithScore.moveString, 100, y + ITEM_HEIGHT / 2);
        }
    }

    private static String getScoreString(double score) {
        if (AnalysisResult.isDraw(score)) {
            return "(Draw)";
        } else if (AnalysisResult.isWin(score)) {
            return "(Win)";
        } else if (AnalysisResult.isLoss(score)) {
            return "(Loss)";
        } else {
            long playerScore = Math.round(100 * score);
            double roundScore = playerScore / 100.0;
            return String.format("(%.2f)", Double.valueOf(roundScore));
        }
    }

    @Override
    public double getWidth() {
        boolean scrollBar = (analyzedMoves.size() * ITEM_HEIGHT) > cl.getHeight();
        return scrollBar ? cl.getWidth() - EScrollBar.BAR_WIDTH : cl.getWidth();
    }

    @Override
    public double getHeight() {
        return Math.max(cl.getHeight(), analyzedMoves.size() * ITEM_HEIGHT - 1);
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
