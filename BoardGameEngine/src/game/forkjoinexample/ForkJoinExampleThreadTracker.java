package game.forkjoinexample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Pair;

public class ForkJoinExampleThreadTracker {
	public static int SLEEP_PER_EVAL = 128;
	public static int SLEEP_PER_BRANCH = 32;
	public static int SLEEP_PER_MERGE = 16;

	private static List<List<ForkJoinExampleNode>> nodesByDepth = new ArrayList<>();
	private static Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> nodeToInfoMap = new HashMap<>();

	public static synchronized void init(ForkJoinExampleTree tree) {
		List<List<ForkJoinExampleNode>> newNodesByDepth = new ArrayList<>();
		buildList(newNodesByDepth, tree.getCurrentNode(), 0);
		Map<ForkJoinExampleNode, ForkJoinExampleNodeInfo> newNodeInfoMap = buildNodeInfoMap(newNodesByDepth);
		nodesByDepth = newNodesByDepth;
		nodeToInfoMap = newNodeInfoMap;
	}

	public static ForkJoinExampleNode getRoot() {
		return nodesByDepth.get(0).get(0);
	}

	public static void setSleepTimes(int eval, int branch, int merge) {
		SLEEP_PER_EVAL = eval;
		SLEEP_PER_BRANCH = branch;
		SLEEP_PER_MERGE = merge;
	}

	public static synchronized void clearInfo() {
		for (ForkJoinExampleNodeInfo nodeInfo : nodeToInfoMap.values()) {
			nodeInfo.clearInfo();
		}
	}

	// 1
	// 2 5
	// 3 4 6 7
	private static void buildList(List<List<ForkJoinExampleNode>> newnodesByDepth, ForkJoinExampleNode currentNode, int depth) {
		if (depth + 1 > newnodesByDepth.size()) {
			newnodesByDepth.add(new ArrayList<>());
		}
		newnodesByDepth.get(depth).add(currentNode);
		for (ForkJoinExampleNode child : currentNode.getChildren()) {
			buildList(newnodesByDepth, child, depth + 1);
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

	public static synchronized List<List<ForkJoinExampleNode>> nodesByDepth() {
		return nodesByDepth;
	}

	public static ForkJoinExampleNodeInfo getForkJoinExampleNodeInfo(ForkJoinExampleNode node) {
		return nodeToInfoMap.get(node);
	}

	public static void setThreadName(ForkJoinExampleNode node, long sleep) {
		sleep(sleep);
		nodeToInfoMap.get(node).setThreadName(Thread.currentThread().getName());
	}

	public static void setForked(ForkJoinExampleNode parentMove) {
		nodeToInfoMap.get(parentMove).setForked();

	}

	public static void branchVisited(ForkJoinExampleNode parent, ForkJoinExampleNode child, long sleep) {
		sleep(sleep);
		nodeToInfoMap.get(parent).addChild(child);
	}

	static void sleep(long sleep) {
		if (sleep == 0) {
			return;
		}
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	static class ForkJoinExampleNodeInfo {
		final double fractionX;
		final double fractionY;
		private String threadName;
		private final Map<ForkJoinExampleNode, Pair<ForkJoinExampleNodeInfo, String>> childMap = new HashMap<>(); // child -> (info, thread name)
		private boolean isForked = false;

		public ForkJoinExampleNodeInfo(double fractionX, double fractionY) {
			this.fractionX = fractionX;
			this.fractionY = fractionY;
		}

		public synchronized void clearInfo() {
			threadName = null;
			isForked = false;
			childMap.clear();
		}

		public String getThreadName() {
			return threadName;
		}

		public void setThreadName(String threadName) {
			this.threadName = threadName;
		}

		public boolean isForked() {
			return isForked;
		}

		public void setForked() {
			isForked = true;
		}

		public synchronized Collection<Pair<ForkJoinExampleNodeInfo, String>> getChildren() {
			return new ArrayList<>(childMap.values());
		}

		public synchronized void addChild(ForkJoinExampleNode child) {
			childMap.put(child, Pair.valueOf(ForkJoinExampleThreadTracker.getForkJoinExampleNodeInfo(child), Thread.currentThread().getName()));
		}
	}
}
