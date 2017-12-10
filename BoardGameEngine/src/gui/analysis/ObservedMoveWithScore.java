package gui.analysis;

public class ObservedMoveWithScore {
	String moveString;
	double score;
	boolean isPartial;

	public ObservedMoveWithScore(String moveString, double score, boolean isPartial) {
		this.moveString = moveString;
		this.score = score;
		this.isPartial = isPartial;
	}
}