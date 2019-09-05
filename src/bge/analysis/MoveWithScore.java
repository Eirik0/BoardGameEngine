package bge.analysis;

import java.util.Objects;

public class MoveWithScore<M> {
    public final M move;
    public final double score;

    public MoveWithScore(M move, double score) {
        this.move = move;
        this.score = score;
    }

    public MoveWithScore<M> transform(boolean isCurrentPlayer) {
        return isCurrentPlayer ? this : new MoveWithScore<>(move, -score);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + ((move == null) ? 0 : move.hashCode())) + Double.hashCode(score);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MoveWithScore<?> other = (MoveWithScore<?>) obj;
        return Objects.equals(move, other.move)
                && (score == other.score || AnalysisResult.isDraw(score) && AnalysisResult.isDraw(other.score));
    }

    @Override
    public String toString() {
        return toString(move, score);
    }

    public static <M> String toString(M move, double score) {
        return (move == null ? "null move" : move.toString()) + ": " + Double.toString(score);
    }
}
