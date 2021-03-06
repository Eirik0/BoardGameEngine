package bge.game.forkjoinexample;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bge.game.forkjoinexample.ForkJoinExampleThreadTracker.ChildInfo;
import bge.game.forkjoinexample.ForkJoinExampleThreadTracker.ForkJoinExampleNodeInfo;
import bge.gui.gamestate.IGameRenderer;
import bge.igame.MoveList;
import bge.main.BoardGameEngineMain;
import gt.component.ComponentCreator;
import gt.component.IMouseTracker;
import gt.gameentity.DrawingMethods;
import gt.gameentity.IGraphics;
import gt.gamestate.UserInput;
import gt.util.EMath;

public class ForkJoinExampleGameRenderer implements IGameRenderer<ForkJoinExampleNode, ForkJoinExampleTree> {
    private static final int BREDTH = (int) Math.round(Math.pow(ForkJoinExampleTree.DEPTH, ForkJoinExampleTree.BRANCHING_FACTOR));

    private final Map<String, Color> threadColorMap = new HashMap<>();
    private final Random random = new Random();

    private double nodeRadius = 0;

    private double width;
    private double height;

    @SuppressWarnings("unused")
    public ForkJoinExampleGameRenderer(IMouseTracker mouseTracker) {
    }

    @Override
    public void initializeAndDrawBoard(IGraphics g, double imageWidth, double imageHeight) {
        width = imageHeight;
        height = imageHeight;
        int padding = 20; // pixels on either side
        nodeRadius = ((width - 2 * padding) / BREDTH) / 4 + 0.5;
        g.setColor(ComponentCreator.backgroundColor());
        g.fillRect(0, 0, width, height);
    }

    @Override
    public void drawPosition(IGraphics g, ForkJoinExampleTree position, MoveList<ForkJoinExampleNode> possibleMoves, ForkJoinExampleNode lastMove) {
        g.setFont(BoardGameEngineMain.DEFAULT_SMALL_FONT);
        int fontHeight = EMath.round(g.getStringDimensions("N").getSecond()) + 2;
        g.setColor(ComponentCreator.foregroundColor());
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
                        : ComponentCreator.foregroundColor();
                double nodeX = nodeInfo.fractionX * width;
                double nodeY = nodeInfo.fractionY * height;
                // draw node
                if (nodeInfo.isForked()) {
                    g.setColor(color);
                    g.fillCircle(nodeX, nodeY, nodeRadius * 2);
                } else {
                    g.setColor(color);
                    g.drawCircle(nodeX, nodeY, nodeRadius);
                    // maybe fill in node
                    if (nodeInfo.getThreadName() != null) {
                        g.fillCircle(nodeX, nodeY, nodeRadius * 2);
                    }
                }
                // draw lines to children
                for (ChildInfo child : nodeInfo.getChildren()) {
                    g.setColor(getColorFromThreadName(child.threadName, child.branchTime));
                    ForkJoinExampleNodeInfo childInfo = child.childInfo;
                    g.drawLine(nodeX, nodeY, childInfo.fractionX * width, childInfo.fractionY * height);
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
        return DrawingMethods.fadeToColor(Color.WHITE, color, calculateDecay(lastUpdateTime));
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
