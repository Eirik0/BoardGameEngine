package gui.analysis;

public class ObservedMoveWithScore {
	String move;
	double score;
	boolean isPartial;

	public ObservedMoveWithScore(String moveString, double score, boolean isPartial) {
		move = moveString;
		this.score = score;
		this.isPartial = isPartial;
	}
}