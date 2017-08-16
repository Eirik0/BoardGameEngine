package game.forkjoinexample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Pair;

public class ForkJoinExampleThreadTracker {
	public static final int SLEEP_PER_EVAL = 125;
	public static final int SLEEP_PER_MERGE = 63;

	private static Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> nodeToInfoMap = new HashMap<>();
	private static List<List<ForkJoinExampleNode>> nodesByBredth = Collections.synchronizedList(new ArrayList<>());

	public static synchronized void init(ForkJoinExampleTree tree) {
		nodesByBredth.clear();
		nodeToInfoMap.clear();
		buildList(tree.getCurrentNode(), 0);
		buildNodeInfoMap();
	}

	//   1
	// 2   5
	//3 4 6 7
	private static void buildList(ForkJoinExampleNode currentNode, int depth) {
		if (depth + 1 > nodesByBredth.size()) {
			nodesByBredth.add(Collections.synchronizedList(new ArrayList<>()));
		}
		nodesByBredth.get(depth).add(currentNode);
		for (ForkJoinExampleNode child : currentNode.getChildren()) {
			buildList(child, depth + 1);
		}
	}

	private static void buildNodeInfoMap() {
		double fractionY = 1.0 / (ForkJoinExampleGame.DEPTH + 1); // + 1 for extra space
		double currentFractionY = fractionY;
		for (List<ForkJoinExampleNode> nodes : nodesByBredth) {
			double fractionX = 1.0 / (nodes.size() * 2);
			double currentFractionX = fractionX;
			for (ForkJoinExampleNode node : nodes) {
				nodeToInfoMap.put(node, new ForkJoinExampleNodeInfo(currentFractionX, currentFractionY));
				currentFractionX += 2 * fractionX;
			}
			currentFractionY += fractionY;
		}
	}

	public static synchronized List<List<ForkJoinExampleNode>> getNodesByBredth() {
		return nodesByBredth;
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
