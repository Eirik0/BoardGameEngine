package analysis;

import java.util.Objects;

public class MoveWithScore<M> {
	public final M move;
	public final double score;

	private boolean isValid = true;

	public MoveWithScore(M move, double score) {
		this(move, score, true);
	}

	public MoveWithScore(M move, double score, boolean isValid) {
		this.move = move;
		this.score = score;
		this.isValid = isValid;
	}

	public boolean isValid() {
		return isValid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		long doubleToLong = Double.doubleToLongBits(score);
		return prime * (prime + ((move == null) ? 0 : move.hashCode())) + (int) (doubleToLong ^ (doubleToLong >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		MoveWithScore<?> other = (MoveWithScore<?>) obj;
		return Objects.equals(move, other.move) && (score == other.score || AnalysisResult.isDraw(score) && AnalysisResult.isDraw(other.score));
	}

	@Override
	public String toString() {
		return (move == null ? "null move" : move.toString()) + ": " + score;
	}
}
