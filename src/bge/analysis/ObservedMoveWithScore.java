package bge.analysis;

public class ObservedMoveWithScore {
    public final String moveString;
    public final double score;
    public final boolean isPartial;

    public ObservedMoveWithScore(String moveString, double score, boolean isPartial) {
        this.moveString = moveString;
        this.score = score;
        this.isPartial = isPartial;
    }
}