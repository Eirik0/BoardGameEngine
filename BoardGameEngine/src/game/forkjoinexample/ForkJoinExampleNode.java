package game.forkjoinexample;

public class ForkJoinExampleNode {
	private final int number;

	private ForkJoinExampleNode parent;
	private final ForkJoinExampleNode[] children;

	public ForkJoinExampleNode(int number, ForkJoinExampleNode[] children) {
		this.number = number;
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
		return "Node " + number;
	}
}
