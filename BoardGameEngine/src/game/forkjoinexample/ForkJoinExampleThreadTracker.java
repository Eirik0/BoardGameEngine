package game.forkjoinexample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Pair;

public class ForkJoinExampleThreadTracker {
	public static final int SLEEP_PER_EVAL = 125;
	public static final int SLEEP_PER_BRANCH = 63;
	public static final int SLEEP_PER_MERGE = 15;

	private static List<List<ForkJoinExampleNode>> nodesByDepth = new ArrayList<>();
	private static Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> nodeToInfoMap = new HashMap<>();

	public static synchronized void init(ForkJoinExampleTree tree) {
		List<List<ForkJoinExampleNode>> newNodesByDepth = new ArrayList<>();
		buildList(newNodesByDepth, tree.getCurrentNode(), 0);
		Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> newNodeInfoMap = buildNodeInfoMap(newNodesByDepth);
		nodesByDepth = newNodesByDepth;
		nodeToInfoMap = newNodeInfoMap;
	}

	public static synchronized void clearInfo() {
		for (ForkJoinExampleNodeInfo nodeInfo : nodeToInfoMap.values()) {
			nodeInfo.clearInfo();
		}
	}

	//   1
	// 2   5
	//3 4 6 7
	private static void buildList(List<List<ForkJoinExampleNode>> newNodesByBredth, ForkJoinExampleNode currentNode, int depth) {
		if (depth + 1 > newNodesByBredth.size()) {
			newNodesByBredth.add(new ArrayList<>());
		}
		newNodesByBredth.get(depth).add(currentNode);
		for (ForkJoinExampleNode child : currentNode.getChildren()) {
			buildList(newNodesByBredth, child, depth + 1);
		}
	}

	private static Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> buildNodeInfoMap(List<List<ForkJoinExampleNode>> newNodesByDepth) {
		Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> newNodeToInfoMap = new HashMap<>();
		double fractionY = 1.0 / (ForkJoinExampleGame.DEPTH + 1); // + 1 for extra space
		double currentFractionY = fractionY;
		for (List<ForkJoinExampleNode> nodes : newNodesByDepth) {
			double fractionX = 1.0 / (nodes.size() * 2);
			double currentFractionX = fractionX;
			for (ForkJoinExampleNode node : nodes) {
				newNodeToInfoMap.put(node, new ForkJoinExampleNodeInfo(currentFractionX, currentFractionY));
				currentFractionX += 2 * fractionX;
			}
			currentFractionY += fractionY;
		}
		return newNodeToInfoMap;
	}

	public static synchronized List<List<ForkJoinExampleNode>> getNodesByBredth() {
		return nodesByDepth;
	}

	public static ForkJoinExampleNodeInfo getForkJoinExampleNodeInfo(ForkJoinExampleNode node) {
		return nodeToInfoMap.get(node);
	}

	public static void setThreadName(ForkJoinExampleNode node, long sleep) {
		nodeToInfoMap.get(node).setThreadName(Thread.currentThread().getName());
		sleep(sleep);
	}

	private static void sleep(long sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void branchVisited(ForkJoinExampleNode parent, ForkJoinExampleNode child, long sleep) {
		nodeToInfoMap.get(parent).addChild(child);
		sleep(sleep);
	}

	static class ForkJoinExampleNodeInfo {
		final double fractionX;
		final double fractionY;
		private String threadName;
		private final Map<ForkJoinExampleNode, Pair<ForkJoinExampleNodeInfo, String>> childMap = new HashMap<>(); // child -> info,threadname

		public ForkJoinExampleNodeInfo(double fractionX, double fractionY) {
			this.fractionX = fractionX;
			this.fractionY = fractionY;
		}

		public void clearInfo() {
			threadName = null;
			childMap.clear();
		}

		public String getThreadName() {
			return threadName;
		}

		public void setThreadName(String threadName) {
			this.threadName = threadName;
		}

		public Collection<Pair<ForkJoinExampleNodeInfo, String>> getChildren() {
			return childMap.values();
		}

		public void addChild(ForkJoinExampleNode child) {
			childMap.put(child, Pair.valueOf(ForkJoinExampleThreadTracker.getForkJoinExampleNodeInfo(child), Thread.currentThread().getName()));
		}
	}
}
