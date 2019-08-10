package analysis;

public class MoveAnalysis {
    public final double score;

    public MoveAnalysis(double score) {
        this.score = score;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        long longBits = Double.doubleToLongBits(score);
        return prime + (int) (longBits ^ (longBits >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MoveAnalysis other = (MoveAnalysis) obj;
        return score == other.score;
    }

    @Override
    public String toString() {
        return Double.toString(score);
    }
}
