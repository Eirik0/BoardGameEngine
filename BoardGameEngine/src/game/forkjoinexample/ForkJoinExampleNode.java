package game.forkjoinexample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ForkJoinExampleNode {
	private final Integer number;
	public final double score = new Random().nextDouble() * 10 - 5;

	private ForkJoinExampleNode parent;
	private final ForkJoinExampleNode[] children;

	public ForkJoinExampleNode(int number, ForkJoinExampleNode[] children) {
		this.number = Integer.valueOf(number);
		this.children = children;
		for (ForkJoinExampleNode child : children) {
			child.setParent(this);
		}
	}

	public ForkJoinExampleNode getParent() {
		return parent;
	}

	private void setParent(ForkJoinExampleNode parent) {
		this.parent = parent;
	}

	public ForkJoinExampleNode[] getChildren() {
		return children;
	}

	@Override
	public String toString() {
		List<Integer> nodeList = new ArrayList<>();
		nodeList.add(number);
		ForkJoinExampleNode parentNode = parent;
		if (parentNode == null) {
			return number.toString();
		}
		while (parentNode != null) {
			nodeList.add(parentNode.number);
			parentNode = parentNode.parent;
		}
		StringBuilder sb = new StringBuilder();
		int i = nodeList.size() - 1;
		do {
			sb.append(nodeList.get(i)).append("->");
			--i;
		} while (i > 0);
		sb.append(nodeList.get(0));
		return sb.toString();
	}
}
