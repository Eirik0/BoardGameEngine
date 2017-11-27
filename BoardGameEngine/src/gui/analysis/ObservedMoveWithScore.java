package gui.analysis;

public class ObservedMoveWithScore {
	String move;
	double score;
	boolean isPartial;

	public ObservedMoveWithScore(Object move, double score, boolean isPartial) {
		this.move = move.toString();
		this.score = score;
		this.isPartial = isPartial;
	}
}