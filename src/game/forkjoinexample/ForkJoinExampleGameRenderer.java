package game.forkjoinexample;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.MoveList;
import game.forkjoinexample.ForkJoinExampleThreadTracker.ChildInfo;
import game.forkjoinexample.ForkJoinExampleThreadTracker.ForkJoinExampleNodeInfo;
import gui.GameGuiManager;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;
import main.BoardGameEngineMain;

public class ForkJoinExampleGameRenderer implements IGameRenderer<ForkJoinExampleNode, ForkJoinExampleTree> {
    private static final int BREDTH = (int) Math.round(Math.pow(ForkJoinExampleTree.DEPTH, ForkJoinExampleTree.BRANCHING_FACTOR));

    private final Map<String, Color> threadColorMap = new HashMap<>();
    private final Random random = new Random();

    private double nodeRadius = 0;

    @Override
    public void initializeAndDrawBoard(Graphics2D g) {
        int padding = 20; // pixels on either side
        nodeRadius = (((double) GameGuiManager.getComponentWidth() - 2 * padding) / BREDTH) / 4 + 0.5;
        g.setColor(BoardGameEngineMain.BACKGROUND_COLOR);
        g.fillRect(0, 0, GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
    }

    @Override
    public void drawPosition(Graphics2D g, ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> possibleMoves, ForkJoinExampleNode lastMove) {
        int width = GameGuiManager.getComponentWidth();
        int height = GameGuiManager.getComponentHeight();
        g.setFont(BoardGameEngineMain.DEFAULT_SMALL_FONT);
        int fontHeight = g.getFontMetrics().getHeight() + 2;
        g.setColor(BoardGameEngineMain.FOREGROUND_COLOR);
        ForkJoinExampleThreadTracker.maybeRecalculateTimeElapsed();
        g.drawString("Nodes per evaluated second: " + String.format("%.2f", Double.valueOf(ForkJoinExampleThreadTracker.getNodesEvaluatedPerSecond())), 2,
                fontHeight);
        g.drawString("Percent reevaluated: " + String.format("%.2f", Double.valueOf(ForkJoinExampleThreadTracker.getPercentReevaluated())), 0, fontHeight * 2);
        g.drawString(
                "Effective evaluations second: " + String.format("%.2f", Double.valueOf(ForkJoinExampleThreadTracker.getEffectiveNodesEvaluatedPerSecond())), 2,
                fontHeight * 3);
        List<List<ForkJoinExampleNode>> nodesByDepth = ForkJoinExampleThreadTracker.nodesByDepth();
        for (List<ForkJoinExampleNode> nodes : nodesByDepth) {
            for (ForkJoinExampleNode node : nodes) {
                ForkJoinExampleNodeInfo nodeInfo = ForkJoinExampleThreadTracker.getForkJoinExampleNodeInfo(node);
                if (nodeInfo == null) {
                    continue;
                }
                Color color = nodeInfo.getThreadName() != null ? getColorFromThreadName(nodeInfo.getThreadName(), nodeInfo.getThreadSetTime())
                        : BoardGameEngineMain.FOREGROUND_COLOR;
                double nodeX = nodeInfo.fractionX * width;
                double nodeY = nodeInfo.fractionY * height;
                // draw node
                if (nodeInfo.isForked()) {
                    g.setColor(color);
                    fillCircle(g, nodeX, nodeY, nodeRadius * 2);
                } else {
                    g.setColor(color);
                    drawCircle(g, nodeX, nodeY, nodeRadius);
                    // maybe fill in node
                    if (nodeInfo.getThreadName() != null) {
                        fillCircle(g, nodeX, nodeY, nodeRadius * 2);
                    }
                }
                // draw lines to children
                for (ChildInfo child : nodeInfo.getChildren()) {
                    g.setColor(getColorFromThreadName(child.threadName, child.branchTime));
                    ForkJoinExampleNodeInfo childInfo = child.childInfo;
                    g.drawLine(round(nodeX), round(nodeY), round(childInfo.fractionX * width), round(childInfo.fractionY * height));
                }
            }
        }
    }

    private Color getColorFromThreadName(String threadName, long lastUpdateTime) {
        Color color = threadColorMap.get(threadName);
        if (color == null) {
            color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
            threadColorMap.put(threadName, color);
        }
        return decayToColor(color, calculateDecay(lastUpdateTime));
    }

    private static double calculateDecay(long lastUpdateTime) {
        double secondsElapsed = (System.nanoTime() - lastUpdateTime) / 1000000000.0;
        return Math.pow(Math.E, -secondsElapsed);
    }

    @Override
    public ForkJoinExampleNode maybeGetUserMove(UserInput input, ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> possibleMoves) {
        return null; // only the computer plays this game
    }
}
