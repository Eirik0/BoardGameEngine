package game.forkjoinexample;

import game.forkjoinexample.ForkJoinExampleThreadTracker.ForkJoinExampleNodeInfo;
import gui.GameGuiManager;
import gui.gamestate.GameState.UserInput;
import gui.gamestate.IGameRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.Pair;

public class ForkJoinExampleGameRenderer implements IGameRenderer<ForkJoinExampleNode, ForkJoinExampleTree> {
	private static final int BREDTH = (int) Math.round(Math.pow(ForkJoinExampleGame.DEPTH, ForkJoinExampleGame.BRANCHING_FACTOR));

	private final Map<String, Color> threadColorMap = new HashMap<>();
	private final Random random = new Random();

	private double nodeRadius = 0;

	@Override
	public void initializeAndDrawBoard(Graphics2D g) {
		int padding = 20; // pixels on either side
		nodeRadius = (((double) GameGuiManager.getComponentWidth() - 2 * padding) / BREDTH) / 4;
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GameGuiManager.getComponentWidth(), GameGuiManager.getComponentHeight());
	}

	@Override
	public void drawPosition(Graphics2D g, ForkJoinExampleTree position) {
		int width = GameGuiManager.getComponentWidth();
		int height = GameGuiManager.getComponentHeight();
		List<List<ForkJoinExampleNode>> nodesByDepth = ForkJoinExampleThreadTracker.nodesByDepth();
		for (List<ForkJoinExampleNode> nodes : nodesByDepth) {
			for (ForkJoinExampleNode node : nodes) {
				ForkJoinExampleNodeInfo nodeInfo = ForkJoinExampleThreadTracker.getForkJoinExampleNodeInfo(node);
				if (nodeInfo == null) {
					continue;
				}
				Color color = nodeInfo.getThreadName() != null ? getColorFromThreadName(nodeInfo.getThreadName()) : Color.BLACK;
				double nodeX = nodeInfo.fractionX * width;
				double nodeY = nodeInfo.fractionY * height;
				// draw lines to children

				for (Pair<ForkJoinExampleNodeInfo, String> child : nodeInfo.getChildren()) {
					g.setColor(getColorFromThreadName(child.getSecond()));
					ForkJoinExampleNodeInfo childInfo = child.getFirst();
					g.drawLine(round(nodeX), round(nodeY), round(childInfo.fractionX * width), round(childInfo.fractionY * height));
				}
				// draw node
				if (nodeInfo.isForked()) {
					g.setColor(color);
					fillCircle(g, nodeX, nodeY, nodeRadius * 3);
				} else {
					g.setColor(Color.BLACK);
					drawCircle(g, nodeX, nodeY, nodeRadius);
					// maybe fill in node
					if (nodeInfo.getThreadName() != null) {
						g.setColor(color);
						fillCircle(g, nodeX, nodeY, nodeRadius);
					}
				}
			}
		}
	}

	private Color getColorFromThreadName(String threadName) {
		Color color = threadColorMap.get(threadName);
		if (color == null) {
			color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			threadColorMap.put(threadName, color);
		}
		return color;
	}

	@Override
	public ForkJoinExampleNode maybeGetUserMove(UserInput input, ForkJoinExampleTree position) {
		return null; // only the computer plays this game
	}
}
