package game.forkjoinexample;

import java.util.List;

public class ForkJoinExampleNode {
	private ForkJoinExampleNode parent;
	private final List<ForkJoinExampleNode> children;

	public ForkJoinExampleNode(List<ForkJoinExampleNode> children) {
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

	public List<ForkJoinExampleNode> getChildren() {
		return children;
	}
}
