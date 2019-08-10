package analysis.montecarlo;

import analysis.AnalysisResult;

public class MonteCarloStatistics {
    public static final double WIN = 1;
    public static final double DRAW = 0;
    public static final double LOSS = -1;

    public final int player;

    int numUncertain = 0;
    int numCertain = 0;
    int numWon = 0;
    int numDrawn = 0;
    int numLost = 0;

    boolean isDecided = false;

    public MonteCarloStatistics(int player) {
        this.player = player;
    }

    public MonteCarloStatistics(int player, double score) {
        this.player = player;
        numCertain = 1;
        if (AnalysisResult.isWin(score)) {
            numWon = 1;
        } else if (AnalysisResult.isLoss(score)) {
            numLost = 1;
        } else {
            numDrawn = 1;
        }
        isDecided = true;
    }

    public void addScore(double score) {
        ++numUncertain;
        if (AnalysisResult.isWin(score)) {
            ++numWon;
        } else if (AnalysisResult.isLoss(score)) {
            ++numLost;
        } else {
            ++numDrawn;
        }
    }

    public void updateWith(MonteCarloStatistics result) {
        numUncertain += result.numUncertain;
        numCertain += result.numCertain;
        numDrawn += result.numDrawn;
        if (player == result.player) {
            numWon += result.numWon;
            numLost += result.numLost;
        } else {
            numWon += result.numLost;
            numLost += result.numWon;
        }
    }

    public void setDecided() {
        isDecided = true;
        numCertain += numUncertain;
        numUncertain = 0;
        if (numWon > 0) {
            numWon = numCertain;
            numDrawn = 0;
            numLost = 0;
        } else if (numDrawn > 0) {
            numDrawn = numCertain;
            numLost = 0;
        } else {
            numLost = numCertain;
        }
    }

    public void setResult(MonteCarloStatistics statistics) {
        numUncertain = statistics.numUncertain;
        numCertain = statistics.numCertain;
        numWon = statistics.numWon;
        numDrawn = statistics.numDrawn;
        numLost = statistics.numLost;
        isDecided = statistics.isDecided;
    }

    public int getTotalNodesEvaluated() {
        return numCertain + numUncertain;
    }

    public boolean isWin(int currentPlayer) {
        return isDecided && ((player == currentPlayer && numWon > 0) || (player != currentPlayer && numLost > 0));
    }

    public double getExpectedValue(int parentNodesEvaluated) {
        return getMeanValue() + getUncertainty(parentNodesEvaluated);
    }

    public double getUncertainty(int parentNumUncertain) {
        return isDecided || parentNumUncertain == 0 ? 0.0 : Math.sqrt(2 * Math.log(parentNumUncertain) / numUncertain);
    }

    public double getMeanValue() {
        if (isDecided) {
            return numWon > 0 ? WIN : numDrawn > DRAW ? 0.0 : LOSS;
        }
        return getTotalNodesEvaluated() == 0 ? 0.0 : (double) (numWon - numLost) / getTotalNodesEvaluated();
    }

    @Override
    public String toString() {
        return player + ": " + numWon + " won, " + numDrawn + " drawn, " + numLost + " lost, " + getTotalNodesEvaluated() + " total";
    }

    public String toString(int parentNodesEvaluated) {
        return toString() + "; " + String.format("%.2f +/- %.2f", Double.valueOf(getMeanValue()), Double.valueOf(getUncertainty(parentNodesEvaluated)));
    }
}
