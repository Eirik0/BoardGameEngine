package bge.analysis;

import java.util.Objects;

public class AnalyzedMove<M> {
    public final M move;
    public final MoveAnalysis analysis; // XXX Change to score

    public AnalyzedMove(M move, double score) {
        this.move = move;
        this.analysis = new MoveAnalysis(score);
    }

    public AnalyzedMove<M> transform(boolean isCurrentPlayer) {
        return isCurrentPlayer ? this : new AnalyzedMove<>(move, -analysis.score);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + ((move == null) ? 0 : move.hashCode())) + analysis.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AnalyzedMove<?> other = (AnalyzedMove<?>) obj;
        return Objects.equals(move, other.move)
                && (analysis == other.analysis || AnalysisResult.isDraw(analysis.score) && AnalysisResult.isDraw(other.analysis.score));
    }

    @Override
    public String toString() {
        return toString(move, analysis);
    }

    public static <M> String toString(M move, MoveAnalysis score) {
        return (move == null ? "null move" : move.toString()) + ": " + score.toString();
    }
}
